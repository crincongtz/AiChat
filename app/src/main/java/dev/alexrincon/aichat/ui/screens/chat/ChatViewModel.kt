package dev.alexrincon.aichat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            // Añadir el mensaje del usuario
            val userMessage = Message(
                content = message,
                isFromUser = true
            )

            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + userMessage,
                    isLoading = true
                )
            }

            // Simular la respuesta del bot
            val botMessage = Message(
                content = "Hola! Soy un bot de ejemplo. ¿En qué puedo ayudarte?",
                isFromUser = false
            )

            // Añadir pequeño retraso para simular la respuesta
            kotlinx.coroutines.delay(1000)

            // Actualizar el estado con la respuesta del bot
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + botMessage,
                    isLoading = false
                )
            }
        }

        // TODO: Aquí más adelante enviaremos el mensaje a la API de OpenAI
        // y añadiremos la respuesta a la lista de mensajes
    }
}

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
