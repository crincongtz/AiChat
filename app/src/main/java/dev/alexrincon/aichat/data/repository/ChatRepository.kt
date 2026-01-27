package dev.alexrincon.aichat.data.repository

import dev.alexrincon.aichat.data.datasource.OpenAIDataSource
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que gestiona las operaciones de chat.
 * Mantiene el historial de conversación completo y expone un Flow para que
 * otros componentes puedan observar los cambios.
 */
@Singleton
class ChatRepository @Inject constructor(
    private val openAIDataSource: OpenAIDataSource
) {
    private val _currentConversation = MutableStateFlow<List<ChatMessage>>(emptyList())

    val currentConversation: StateFlow<List<ChatMessage>> = _currentConversation.asStateFlow()

    init {
        val systemMessage = createSystemMessage(
             "Eres un asistente muy útil y amigable. Responde de manera clara y concisa",
        )
        _currentConversation.value = listOf(systemMessage)
    }

    suspend fun sendMessage(content: String): Result<Unit> {
        if (content.isBlank()) return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío."))

        try {
            val userMessage = createUserMessage(content)
            addMessagesToConversation(userMessage)

            // IA response
            val response = openAIDataSource.getChatCompletion(_currentConversation.value)

            val assistantMessage = createAssistantMessage(response)
            addMessagesToConversation(assistantMessage)

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun addMessagesToConversation(message: ChatMessage) {
        val currentMessages = _currentConversation.value.toMutableList()
        currentMessages.add(message)
        _currentConversation.value = currentMessages
    }

    fun createUserMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.USER
        )
    }

    fun createAssistantMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.ASSISTANT
        )
    }

    fun createSystemMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.SYSTEM
        )
    }
}
