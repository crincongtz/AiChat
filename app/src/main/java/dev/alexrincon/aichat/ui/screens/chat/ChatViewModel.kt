package dev.alexrincon.aichat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alexrincon.aichat.data.repository.ChatRepository
import dev.alexrincon.aichat.data.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val chatHistory = mutableListOf<ChatMessage>()

    init {
        val systemMessage = chatRepository.createSystemMessage(
            "Eres un asistente muy útil y amigable."
        )
        chatHistory.add(systemMessage)
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            try {
                // Añadir el mensaje del usuario
                val  userMessage = chatRepository.createUserMessage(message)
                chatHistory.add(userMessage)

                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + userMessage,
                        isLoading = true,
                        error = null
                    )
                }

                // Enviar a OpenAI y obtener respuesta
                val aiResponse = chatRepository.getChatCompletion(chatHistory)

                // Añadir la respuesta de la IA
                val assistantMessage = chatRepository.createAssistantMessage(aiResponse)
                chatHistory.add(assistantMessage)

                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + assistantMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("429") == true -> 
                        "Límite de solicitudes excedido. Por favor, verifica tu cuenta de OpenAI."
                    e.message?.contains("401") == true -> 
                        "API key inválida. Verifica tu configuración."
                    e.message?.contains("quota") == true -> 
                        "Cuota de API excedida. Verifica tu plan en OpenAI."
                    else -> "Error: ${e.message}"
                }
                
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }

}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
