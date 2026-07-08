package com.example.homigo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.homigo.data.model.Match
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen() {
    val coroutineScope = rememberCoroutineScope()
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var activeMatchDetails by remember { mutableStateOf<Match?>(null) }

    fun loadMatches() {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                matches = HomigoRepository.getMatches()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Failed to load matches"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadMatches()
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
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
                Button(onClick = { loadMatches() }) {
                    Text("Retry")
                }
            }
        } else if (matches.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No roommate recommendations found.", fontWeight = FontWeight.SemiBold)
                Text(
                    "Try checking your preferences or wait for other users of LPU to join.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { activeMatchDetails = match },
                        onRequestSent = { loadMatches() }
                    )
                }
            }
        }

        // Weighted breakdown dialog
        activeMatchDetails?.let { match ->
            CompatibilityDetailsDialog(
                match = match,
                onDismiss = { activeMatchDetails = null }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchCard(
    match: Match,
    onClick: () -> Unit,
    onRequestSent: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isSendingRequest by remember { mutableStateOf(false) }
    var requestError by remember { mutableStateOf<String?>(null) }

    val user = match.user

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name ?: "Anonymous User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (user.is_verified == 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("Verified", color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(
                        text = "Hostel: ${user.hostel.uppercase()} | Room: ${user.room_preference}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Overall score badge
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = match.overallScore / 100f,
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "${match.overallScore}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (!user.bio.isNullOrBlank()) {
                Text(
                    text = user.bio,
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            // Fake Profile Warning Tag if risk score is high
            if (user.fake_risk_score >= 0.5) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Fake risk",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Unverified Profile (High Risk Score: ${Math.round(user.fake_risk_score * 100)}%)",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Tags row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SuggestionChip(onClick = {}, label = { Text(user.sleep_schedule.replace("_", " ").capitalize()) })
                SuggestionChip(onClick = {}, label = { Text("Clean: ${user.cleanliness.capitalize()}") })
                SuggestionChip(onClick = {}, label = { Text("Food: ${user.food_preference.capitalize()}") })
                if (user.smoking == "yes") SuggestionChip(onClick = {}, label = { Text("Smokes") })
                if (user.drinking == "yes") SuggestionChip(onClick = {}, label = { Text("Drinks") })
            }

            if (requestError != null) {
                Text(requestError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget: ₹${user.budget_min} - ₹${user.budget_max}/mo",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Request actions based on status
                when (match.requestStatus) {
                    "none" -> {
                        Button(
                            onClick = {
                                isSendingRequest = true
                                requestError = null
                                coroutineScope.launch {
                                    try {
                                        HomigoRepository.sendRequest(user.user_id)
                                        isSendingRequest = false
                                        onRequestSent()
                                    } catch (e: Exception) {
                                        isSendingRequest = false
                                        requestError = e.message ?: "Failed to send request"
                                    }
                                }
                            },
                            enabled = !isSendingRequest
                        ) {
                            if (isSendingRequest) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            } else {
                                Text("Send Request", fontSize = 12.sp)
                            }
                        }
                    }
                    "pending" -> {
                        val isMeSender = match.requestSender == HomigoRepository.currentUser.value?.id
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(if (isMeSender) "Request Sent" else "Respond in Requests", fontSize = 12.sp)
                        }
                    }
                    "accepted" -> {
                        Button(
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Text("Roommates", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        }
                    }
                    "rejected" -> {
                        Text("Declined", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CompatibilityDetailsDialog(
    match: Match,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Compatibility Breakdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Overall: ${match.overallScore}% Match",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                HorizontalDivider()

                val breakdown = match.breakdown
                val categories = listOf(
                    Triple("Budget Range", breakdown.budget, "20% weight"),
                    Triple("Sleep Schedule", breakdown.sleep, "20% weight"),
                    Triple("Smoking/Drinking Lifestyle", breakdown.lifestyle, "20% weight"),
                    Triple("Cleanliness Preferences", breakdown.cleanliness, "20% weight"),
                    Triple("Social (Food, Pets, Guests)", breakdown.social, "20% weight")
                )

                categories.forEach { (label, score, weight) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("$score%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        LinearProgressIndicator(
                            progress = score / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (score > 70) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = weight,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
