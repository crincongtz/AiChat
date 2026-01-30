package dev.alexrincon.aichat.data.datasource

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage as OpenAIChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.alexrincon.aichat.R
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import dev.alexrincon.aichat.util.StringProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIDataSource @Inject constructor(
    private val openAI: OpenAI,
    private val stringProvider: StringProvider
) {

    suspend fun getChatCompletion(messages: List<ChatMessage>): String {
        val openAIMessages = messages.map { it.toOpenAIChatMessage() }

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = openAIMessages
        )

        val completion: ChatCompletion = openAI.chatCompletion(completionRequest)
        return completion.choices.first().message.content ?: stringProvider.getString(R.string.error_no_response)
    }

    suspend fun generateConversationTitle(firstUserMessage: String): String {
        val systemPrompt = OpenAIChatMessage(
            role = ChatRole.System,
            content = stringProvider.getString(R.string.title_generation_prompt)
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
        return completion.choices.first().message.content?.trim() ?: stringProvider.getString(R.string.default_conversation_title)
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
