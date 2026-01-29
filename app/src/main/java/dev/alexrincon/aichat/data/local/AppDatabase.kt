package dev.alexrincon.aichat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.alexrincon.aichat.data.local.dao.ConversationDao
import dev.alexrincon.aichat.data.local.dao.MessageDao
import dev.alexrincon.aichat.data.local.entity.ConversationEntity
import dev.alexrincon.aichat.data.local.entity.MessageEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
