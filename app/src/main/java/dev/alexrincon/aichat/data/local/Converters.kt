package dev.alexrincon.aichat.data.local

import androidx.room.TypeConverter
import dev.alexrincon.aichat.data.model.MessageRole

class Converters {
    @TypeConverter
    fun fromMessageRole(role: MessageRole): String {
        return role.name
    }

    @TypeConverter
    fun toMessageRole(roleName: String): MessageRole {
        return MessageRole.valueOf(roleName)
    }
}
