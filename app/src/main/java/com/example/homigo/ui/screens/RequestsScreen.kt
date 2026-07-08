package com.example.homigo.ui.screens

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
import com.example.homigo.data.model.RoommateRequest
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

@Composable
fun RequestsScreen() {
    val coroutineScope = rememberCoroutineScope()
    var incomingRequests by remember { mutableStateOf<List<RoommateRequest>>(emptyList()) }
    var sentRequests by remember { mutableStateOf<List<RoommateRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(0) } // 0 for Received, 1 for Sent

    fun loadRequests() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                val res = HomigoRepository.getRequests()
                incomingRequests = res.incoming
                sentRequests = res.sent
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Failed to load requests"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadRequests()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeTab) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Received (${incomingRequests.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Sent (${sentRequests.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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
                    Button(onClick = { loadRequests() }) {
                        Text("Retry")
                    }
                }
            } else {
                val currentList = if (activeTab == 0) incomingRequests else sentRequests

                if (currentList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (activeTab == 0) "No received roommate invites." else "No sent roommate invites.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentList) { req ->
                            RequestItemCard(
                                request = req,
                                isIncoming = activeTab == 0,
                                onActionSuccess = { loadRequests() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(
    request: RoommateRequest,
    isIncoming: Boolean,
    onActionSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = request.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hostel: ${request.hostel.uppercase()} | Room: ${request.room_preference}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Date label
                Text(
                    text = request.created_at.substringBefore("T"),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (!request.bio.isNullOrBlank()) {
                Text(
                    text = "\"${request.bio}\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            }

            if (actionError != null) {
                Text(actionError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            if (isIncoming) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isProcessing = true
                            actionError = null
                            coroutineScope.launch {
                                try {
                                    HomigoRepository.respondToRequest(request.id, "rejected")
                                    isProcessing = false
                                    onActionSuccess()
                                } catch (e: Exception) {
                                    isProcessing = false
                                    actionError = e.message ?: "Failed to decline"
                                }
                            }
                        },
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decline")
                    }

                    Button(
                        onClick = {
                            isProcessing = true
                            actionError = null
                            coroutineScope.launch {
                                try {
                                    HomigoRepository.respondToRequest(request.id, "accepted")
                                    isProcessing = false
                                    onActionSuccess()
                                } catch (e: Exception) {
                                    isProcessing = false
                                    actionError = e.message ?: "Failed to accept"
                                }
                            }
                        },
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status: ${request.status.capitalize()}",
                        fontWeight = FontWeight.Bold,
                        color = when (request.status) {
                            "accepted" -> MaterialTheme.colorScheme.primary
                            "rejected" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outline
                        },
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
