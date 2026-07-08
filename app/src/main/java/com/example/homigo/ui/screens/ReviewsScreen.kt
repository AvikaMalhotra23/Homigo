package com.example.homigo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.model.Profile
import com.example.homigo.data.model.Review
import com.example.homigo.data.model.ReviewSummary
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch

@Composable
fun ReviewsScreen() {
    val coroutineScope = rememberCoroutineScope()
    var activeTab by remember { mutableStateOf(0) } // 0 for View Reviews, 1 for Leave Review

    var roommates by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var selectedReviewee by remember { mutableStateOf<Profile?>(null) }
    var reviewSummary by remember { mutableStateOf<ReviewSummary?>(null) }
    var isLoadingSummary by remember { mutableStateOf(false) }

    // Form states
    var selectRoommateToRate by remember { mutableStateOf<Profile?>(null) }
    var cleanliness by remember { mutableStateOf(5) }
    var respect by remember { mutableStateOf(5) }
    var timeliness by remember { mutableStateOf(5) }
    var noise by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    var formSuccessMessage by remember { mutableStateOf<String?>(null) }

    fun loadRoommates() {
        coroutineScope.launch {
            try {
                roommates = HomigoRepository.getChatList()
                if (roommates.isNotEmpty()) {
                    selectedReviewee = roommates.first()
                    selectRoommateToRate = roommates.first()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun loadReviewSummary(userId: Int) {
        isLoadingSummary = true
        coroutineScope.launch {
            try {
                reviewSummary = HomigoRepository.getReviews(userId)
                isLoadingSummary = false
            } catch (e: Exception) {
                isLoadingSummary = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadRoommates()
    }

    LaunchedEffect(selectedReviewee) {
        selectedReviewee?.let {
            loadReviewSummary(it.user_id)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Roommate Reviews", fontWeight = FontWeight.Bold) })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Write Review", fontWeight = FontWeight.Bold) })
        }

        if (activeTab == 0) {
            // View Reviews Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (roommates.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No roommate connections found to display reviews.", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    // Roommate selection dropdown mock (Horizontal scroll selection for simplicity)
                    Text("Select Roommate to view stats:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roommates.forEach { r ->
                            FilterChip(
                                selected = selectedReviewee?.user_id == r.user_id,
                                onClick = { selectedReviewee = r },
                                label = { Text(r.name ?: "Roommate") }
                            )
                        }
                    }

                    if (isLoadingSummary) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        reviewSummary?.let { summary ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Average Roommate Rating", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Total Reviews: ${summary.totalReviews}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

                                    RatingBarLabel(label = "Cleanliness", rating = summary.averageCleanliness)
                                    RatingBarLabel(label = "Respect", rating = summary.averageRespect)
                                    RatingBarLabel(label = "Timeliness", rating = summary.averageTimeliness)
                                    RatingBarLabel(label = "Noise (Low is better)", rating = summary.averageNoise)
                                }
                            }

                            Text("Review Descriptions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            
                            if (summary.reviews.isEmpty()) {
                                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("No reviews written for this roommate yet.", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(summary.reviews) { rev ->
                                        Card(modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(rev.reviewer_name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(rev.created_at.substringBefore("T"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                                }
                                                Text(
                                                    text = rev.comment ?: "",
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Write Review Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (roommates.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No roommate connections found to rate.", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    Text("Select Roommate to Rate:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roommates.forEach { r ->
                            FilterChip(
                                selected = selectRoommateToRate?.user_id == r.user_id,
                                onClick = { selectRoommateToRate = r },
                                label = { Text(r.name ?: "Roommate") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Rate Roommate Habits", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                            StarRatingSelector(label = "Cleanliness (Clean room habits)", rating = cleanliness, onRatingChanged = { cleanliness = it })
                            StarRatingSelector(label = "Respect (Quiet hours and privacy)", rating = respect, onRatingChanged = { respect = it })
                            StarRatingSelector(label = "Timeliness (Rent and bill share splits)", rating = timeliness, onRatingChanged = { timeliness = it })
                            StarRatingSelector(label = "Noise (Low noise level)", rating = noise, onRatingChanged = { noise = it })
                        }
                    }

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment / Feedback on roommate living experience") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (formError != null) {
                        Text(formError!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                    if (formSuccessMessage != null) {
                        Text(formSuccessMessage!!, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val revieweeId = selectRoommateToRate?.user_id ?: return@Button
                            isSubmitting = true
                            formError = null
                            formSuccessMessage = null
                            coroutineScope.launch {
                                try {
                                    HomigoRepository.addReview(revieweeId, cleanliness, respect, timeliness, noise, comment)
                                    isSubmitting = false
                                    formSuccessMessage = "Review submitted successfully!"
                                    comment = ""
                                    // Refresh summary stats
                                    selectedReviewee?.let { loadReviewSummary(it.user_id) }
                                } catch (e: Exception) {
                                    isSubmitting = false
                                    formError = e.message ?: "Failed to submit review"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting && selectRoommateToRate != null
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Submit Review", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBarLabel(label: String, rating: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${rating}/5.0", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
            Row {
                val filledStars = Math.round(rating).toInt()
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= filledStars) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star",
                        tint = if (i <= filledStars) Color(0xFFEAB308) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingSelector(label: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Select Star",
                    tint = if (i <= rating) Color(0xFFEAB308) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRatingChanged(i) }
                )
            }
        }
    }
}
