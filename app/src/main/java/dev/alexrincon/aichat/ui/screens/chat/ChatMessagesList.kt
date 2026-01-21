package dev.alexrincon.aichat.ui.screens.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexrincon.aichat.ui.theme.AiChatTheme

@Composable
fun ChatMessagesList(
    messages: List<Message>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        state = listState
    ) {
        items(messages, key = { it.id }) { message ->
            MessageItem(message = message)
        }

        if (isLoading) {
            item {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (message.isFromUser) {
            Spacer(modifier = Modifier.weight(1f))
        }

        Card(
            modifier = Modifier.padding(4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                color = if (message.isFromUser)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        if (!message.isFromUser) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterStart),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesListPreview() {
    AiChatTheme {
        ChatMessagesList(
            messages = listOf(
                Message(
                    content = "Hola, ¿cómo estás?",
                    isFromUser = true
                ),
                Message(
                    content = "¡Hola! Estoy muy bien, gracias por preguntar. ¿En qué puedo ayudarte hoy?",
                    isFromUser = false
                ),
                Message(
                    content = "Necesito ayuda con un proyecto",
                    isFromUser = true
                )
            ),
            isLoading = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemUserPreview() {
    AiChatTheme {
        MessageItem(
            message = Message(
                content = "Este es un mensaje del usuario",
                isFromUser = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemBotPreview() {
    AiChatTheme {
        MessageItem(
            message = Message(
                content = "Esta es una respuesta del bot",
                isFromUser = false
            )
        )
    }
}
