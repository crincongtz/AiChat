package dev.alexrincon.aichat.data.local.entity

import androidx.compose.material3.TabPosition
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.alexrincon.aichat.data.model.MessageRole

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: Long,
    val content: String,
    val role: MessageRole, // USER, ASSISTANT, SYSTEM
    val createdAt: Long = System.currentTimeMillis(),
)
