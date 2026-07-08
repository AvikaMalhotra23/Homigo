package com.example.homigo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

data class BotMessage(
    val message: String,
    val isUser: Boolean
)

@Composable
fun ChatbotScreen() {
    val coroutineScope = rememberCoroutineScope()
    var messageLog by remember {
        mutableStateOf(
            listOf(
                BotMessage("Hello! I am Homigo, your AI roommate advice chatbot. How can I help you today? You can ask me about budget advice, questions to ask potential roommates, or tips for living together in LPU hostels.", false)
            )
        )
    }

    var inputMessage by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Scroll to the end when new replies arrive
    LaunchedEffect(messageLog.size) {
        if (messageLog.isNotEmpty()) {
            listState.animateScrollToItem(messageLog.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Homigo AI Roommate Assistant", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Text("Ask questions about rental rates, budget advice, roommate compatibility, or moving checklists.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messageLog) { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isUser) 16.dp else 2.dp,
                                    bottomEnd = if (msg.isUser) 2.dp else 16.dp
                                )
                            )
                            .background(
                                if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Ask a question...") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (inputMessage.isBlank() || isSending) return@Button
                    isSending = true
                    val query = inputMessage
                    inputMessage = ""
                    messageLog = messageLog + BotMessage(query, true)

                    coroutineScope.launch {
                        try {
                            val res = HomigoRepository.askChatbot(query)
                            messageLog = messageLog + BotMessage(res.reply, false)
                            isSending = false
                        } catch (e: Exception) {
                            messageLog = messageLog + BotMessage("Error: Failed to fetch reply from assistant. Make sure your server is online.", false)
                            isSending = false
                        }
                    }
                },
                enabled = inputMessage.isNotBlank() && !isSending
            ) {
                Text("Ask")
            }
        }
    }
}
