package dev.alexrincon.aichat.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.alexrincon.aichat.data.local.entity.ConversationEntity
import dev.alexrincon.aichat.data.model.ChatMessage
import dev.alexrincon.aichat.data.model.MessageRole
import dev.alexrincon.aichat.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
            chatRepository.initializeConversation()
        }
    }

    val state: StateFlow<ChatState> = combine(
        chatRepository.currentConversation,
        _uiState
    ) { messages, uiState ->
        ChatState(
            messages = messages.filter { it.role != MessageRole.SYSTEM },
            isLoading = uiState.isLoading,
            error = uiState.error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatState()
    )

    val conversations: StateFlow<List<ConversationEntity>> = chatRepository.allConversations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            chatRepository.sendMessage(message)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error: ${exception.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun createNewConversation() {
        viewModelScope.launch {
            chatRepository.createNewConversationAndSwitch()
        }
    }

    fun switchToConversation(conversationId: Long) {
        chatRepository.switchToConversation(conversationId)
    }

    private data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )
}

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
