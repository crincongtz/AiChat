package dev.alexrincon.aichat.data.mapper

import dev.alexrincon.aichat.data.local.entity.MessageEntity
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole

fun ChatMessage.toEntity(conversationId: Long): MessageEntity {
    return MessageEntity(
        id = this.id,
        conversationId = conversationId,
        content = this.content,
        role = this.role,
        createdAt = System.currentTimeMillis()
    )
}

fun MessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = this.id,
        content = this.content,
        role = this.role
    )
}

fun List<MessageEntity>.toDomain(): List<ChatMessage> {
    return this.map { it.toDomain() }
}
