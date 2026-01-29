package dev.alexrincon.aichat.data.repository

import dev.alexrincon.aichat.data.datasource.OpenAIDataSource
import dev.alexrincon.aichat.data.local.dao.ConversationDao
import dev.alexrincon.aichat.data.local.dao.MessageDao
import dev.alexrincon.aichat.data.local.entity.ConversationEntity
import dev.alexrincon.aichat.data.mapper.toDomain
import dev.alexrincon.aichat.data.mapper.toEntity
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val openAIDataSource: OpenAIDataSource,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    private val _currentConversationId = MutableStateFlow<Long?>(null)

    val currentConversation: Flow<List<ChatMessage>> = _currentConversationId
        .flatMapLatest { conversationId ->
            if (conversationId != null) {
                messageDao.getMessagesByConversationId(conversationId)
                    .map { entities -> entities.toDomain() }
            } else {
                flowOf(emptyList())
            }
        }

    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    suspend fun initializeConversation() {
        if (_currentConversationId.value == null) {
            val lastConversation = conversationDao.getLastConversation()
            
            if (lastConversation == null) {
                val newId = createNewConversation()
                _currentConversationId.value = newId
                
                val systemMessage = createSystemMessage(
                    "Eres un asistente muy útil y amigable. Responde de manera clara y concisa"
                )
                saveMessage(systemMessage)
            } else {
                _currentConversationId.value = lastConversation.id
            }
        }
    }

    suspend fun sendMessage(content: String): Result<Unit> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("El mensaje no puede estar vacío."))
        }

        return try {
            initializeConversation()
            
            val userMessage = createUserMessage(content)
            saveMessage(userMessage)

            val conversationId = getCurrentConversationId()
            updateConversationTitle(conversationId, content)

            val messageEntities = getMessagesSync(conversationId)
            val conversationHistory = messageEntities.toDomain()

            val response = openAIDataSource.getChatCompletion(conversationHistory)

            val assistantMessage = createAssistantMessage(response)
            saveMessage(assistantMessage)

            updateConversationTimestamp()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getMessagesSync(conversationId: Long): List<dev.alexrincon.aichat.data.local.entity.MessageEntity> {
        // Necesitamos acceso directo a Room sin Flow
        return messageDao.getMessagesByConversationIdSync(conversationId)
    }

    private suspend fun createNewConversation(): Long {
        val conversation = ConversationEntity(
            title = "Nueva conversación",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return conversationDao.insertConversation(conversation)
    }

    private suspend fun saveMessage(message: ChatMessage) {
        val conversationId = getCurrentConversationId()
        val messageEntity = message.toEntity(conversationId)
        messageDao.insertMessage(messageEntity)
    }

    private suspend fun updateConversationTimestamp() {
        val conversationId = getCurrentConversationId()
        val conversation = conversationDao.getConversationByIdSync(conversationId)
        conversation?.let {
            conversationDao.updateConversation(
                it.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }

    private suspend fun updateConversationTitle(conversationId: Long, firstMessage: String) {
        val conversation = conversationDao.getConversationByIdSync(conversationId)
        conversation?.let {
            if (it.title == "Nueva conversación") {
                val title = firstMessage.take(50)
                conversationDao.updateConversation(
                    it.copy(title = title)
                )
            }
        }
    }

    suspend fun createNewConversationAndSwitch() {
        val newId = createNewConversation()
        _currentConversationId.value = newId
        
        val systemMessage = createSystemMessage(
            "Eres un asistente muy útil y amigable. Responde de manera clara y concisa"
        )
        saveMessage(systemMessage)
    }

    fun switchToConversation(conversationId: Long) {
        _currentConversationId.value = conversationId
    }

    private fun getCurrentConversationId(): Long {
        return _currentConversationId.value ?: throw IllegalStateException(
            "No hay conversación activa. Llama a initializeConversation() primero."
        )
    }

    private fun createUserMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.USER
        )
    }

    private fun createAssistantMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.ASSISTANT
        )
    }

    private fun createSystemMessage(content: String): ChatMessage {
        return ChatMessage(
            content = content,
            role = MessageRole.SYSTEM
        )
    }
}
