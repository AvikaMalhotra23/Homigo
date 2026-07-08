package com.example.homigo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.model.Profile
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(
    onNavigateToChat: (otherUserId: Int, otherUserName: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var chatList by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadChatList() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                chatList = HomigoRepository.getChatList()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Failed to load chats"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadChatList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { loadChatList() }) {
                    Text("Retry")
                }
            }
        } else if (chatList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No active roommate chats.", fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Send requests in the Explore tab and, once accepted, they will show up here.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatList) { roommate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToChat(roommate.user_id, roommate.name ?: "Roommate") }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = roommate.name ?: "Roommate",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Hostel: ${roommate.hostel.uppercase()} | Room: ${roommate.room_preference}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            Button(
                                onClick = { onNavigateToChat(roommate.user_id, roommate.name ?: "Roommate") }
                            ) {
                                Text("Chat", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
