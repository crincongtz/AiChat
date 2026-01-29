package dev.alexrincon.aichat.data.datasource

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage as OpenAIChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIDataSource @Inject constructor(
    private val openAI: OpenAI
) {

    suspend fun getChatCompletion(messages: List<ChatMessage>): String {
        val openAIMessages = messages.map { it.toOpenAIChatMessage() }

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = openAIMessages
        )

        val completion: ChatCompletion = openAI.chatCompletion(completionRequest)
        return completion.choices.first().message.content ?: "No se recibió respuesta de OpenAI"
    }

    suspend fun generateConversationTitle(firstUserMessage: String): String {
        val systemPrompt = OpenAIChatMessage(
            role = ChatRole.System,
            content = "Genera un título muy corto (máximo 6 palabras) que resuma el tema de la siguiente pregunta o mensaje. Responde SOLO con el título, sin comillas ni puntuación adicional."
        )

        val userPrompt = OpenAIChatMessage(
            role = ChatRole.User,
            content = firstUserMessage
        )

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(systemPrompt, userPrompt)
        )

        val completion: ChatCompletion = openAI.chatCompletion(completionRequest)
        return completion.choices.first().message.content?.trim() ?: "Nueva conversación"
    }

    private fun ChatMessage.toOpenAIChatMessage() : OpenAIChatMessage {
        val role = when (this.role) {
            MessageRole.USER -> ChatRole.User
            MessageRole.ASSISTANT -> ChatRole.Assistant
            MessageRole.SYSTEM -> ChatRole.System
        }
        return OpenAIChatMessage(
            role = role,
            content = content
        )
    }
}
