package dev.alexrincon.aichat.data.repository

import dev.alexrincon.aichat.data.datasource.OpenAIDataSource
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val openAIDataSource: OpenAIDataSource
) {

    suspend fun getChatCompletion(messages: List<ChatMessage>): String {
        return openAIDataSource.getChatCompletion(messages)
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
