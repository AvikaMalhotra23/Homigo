package com.example.homigo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.homigo.data.model.*
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val coroutineScope = rememberCoroutineScope()
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var activeMatchDetails by remember { mutableStateOf<Match?>(null) }

    // Search States
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<UserSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    val currentUserState = HomigoRepository.currentUser.collectAsState()
    val completionState = HomigoRepository.profileCompletion.collectAsState()

    val gender = currentUserState.value?.gender ?: "male"
    val completion = completionState.value

    val activeAccent = remember(gender, completion) {
        val isCompleted = completion >= 100
        if (!isCompleted) Color(0xFF0284C7)
        else if (gender.lowercase() == "female") Color(0xFFEC4899)
        else Color(0xFF2563EB)
    }

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

    // Trigger Search with Debounce
    LaunchedEffect(searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            searchResults = emptyList()
            searchError = null
            return@LaunchedEffect
        }
        kotlinx.coroutines.delay(300)
        isSearching = true
        searchError = null
        try {
            searchResults = HomigoRepository.searchUsers(searchQuery)
        } catch (e: Exception) {
            searchError = e.message ?: "Failed to search users"
        } finally {
            isSearching = false
        }
    }

    FloatingBackground(
        gender = gender,
        completion = completion,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Section Header with Search Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.85f),
                border = BorderStroke(width = 0.5.dp, color = Color.Black.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "DISCOVER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = activeAccent,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Find & Connect with Roommates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Graphite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Liquid Glass style Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = { Text("Search by username or display name...", fontSize = 14.sp, color = Graphite.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Graphite.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Search",
                                        tint = Graphite.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = activeAccent,
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.08f),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF1F5F9).copy(alpha = 0.5f),
                            focusedLabelColor = activeAccent,
                            unfocusedLabelColor = Graphite.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (searchQuery.trim().isNotEmpty()) {
                    // Search Results View
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = activeAccent
                        )
                    } else if (searchError != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = searchError!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 16.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (searchResults.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No students found.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Graphite
                            )
                            Text(
                                text = "Try searching for a different username or display name.",
                                fontSize = 13.sp,
                                color = SecondaryText,
                                modifier = Modifier.padding(top = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        var connectingUserId by remember { mutableStateOf<Int?>(null) }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(searchResults) { result ->
                                SearchResultCard(
                                    result = result,
                                    onClickProfile = {
                                        // Map UserSearchResult to Match for CompatibilityDetailsDialog
                                        if (result.profile != null) {
                                            val profile = Profile(
                                                user_id = result.id,
                                                name = result.displayName ?: result.name,
                                                email = result.email,
                                                gender = result.gender,
                                                college = result.profile.college,
                                                hostel = result.profile.hostel,
                                                room_preference = "shared",
                                                course = result.profile.course,
                                                branch = result.profile.branch,
                                                year = result.profile.year,
                                                semester = 1,
                                                section = null,
                                                roll_number = null,
                                                school = null,
                                                looking_for = "Roommate",
                                                preferred_hostel = result.profile.hostel,
                                                current_hostel = result.profile.hostel,
                                                room_number = null,
                                                move_in_date = null,
                                                interests = null,
                                                languages = null,
                                                hometown = null,
                                                budget_min = 0,
                                                budget_max = 100000,
                                                sleep_schedule = "flexible",
                                                smoking = "no",
                                                drinking = "no",
                                                food_preference = "any",
                                                cleanliness = "moderate",
                                                pets = "no",
                                                guests = "rare",
                                                bio = null,
                                                is_verified = result.profile.is_verified,
                                                id_proof_url = null,
                                                fake_risk_score = 0.0,
                                                username = result.username,
                                                displayName = result.displayName,
                                                avatar = result.avatar
                                            )
                                            activeMatchDetails = Match(
                                                user = profile,
                                                requestStatus = result.requestStatus,
                                                requestSender = result.requestSender,
                                                overallScore = result.compatibilityScore,
                                                breakdown = MatchBreakdown(result.compatibilityScore, 100, 100, 100, 100, 100)
                                            )
                                        }
                                    },
                                    onConnectClick = {
                                        connectingUserId = result.id
                                        coroutineScope.launch {
                                            try {
                                                HomigoRepository.sendRequest(result.id)
                                                // Refresh search results
                                                searchResults = HomigoRepository.searchUsers(searchQuery)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            } finally {
                                                connectingUserId = null
                                            }
                                        }
                                    },
                                    isConnecting = connectingUserId == result.id,
                                    activeAccent = activeAccent,
                                    gender = gender,
                                    completion = completion
                                )
                            }
                        }
                    }
                } else {
                    // Show Default AI Matches List
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = activeAccent
                        )
                    } else if (errorMessage != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 16.dp),
                                fontWeight = FontWeight.Medium
                            )
                            LiquidGlassButton(
                                text = "Retry Loading Matches",
                                onClick = { loadMatches() }
                            )
                        }
                    } else if (matches.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No compatible roommates found.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Graphite
                            )
                            Text(
                                text = "Suggestions will appear as students from your college join Homigo.",
                                fontSize = 13.sp,
                                color = SecondaryText,
                                modifier = Modifier.padding(top = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(matches) { match ->
                                MatchCard(
                                    match = match,
                                    onClick = { activeMatchDetails = match },
                                    onRequestSent = { loadMatches() },
                                    gender = gender,
                                    completion = completion,
                                    activeAccent = activeAccent
                                )
                            }
                        }
                    }
                }

                // Weighted compatibility breakdown dialog
                activeMatchDetails?.let { match ->
                    CompatibilityDetailsDialog(
                        match = match,
                        onDismiss = { activeMatchDetails = null },
                        gender = gender,
                        completion = completion,
                        activeAccent = activeAccent
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    result: UserSearchResult,
    onClickProfile: () -> Unit,
    onConnectClick: () -> Unit,
    isConnecting: Boolean,
    activeAccent: Color,
    gender: String,
    completion: Int
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        density = GlassDensity.MEDIUM,
        onClick = onClickProfile
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture with Online Status Indicator
            Box(contentAlignment = Alignment.Center) {
                CompatibilityRing(
                    score = result.compatibilityScore,
                    avatarEmoji = if (result.gender == "female") "👩" else "👨",
                    modifier = Modifier.size(54.dp),
                    gender = gender,
                    completion = completion
                )
                if (result.isOnline == 1) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.displayName ?: result.name ?: "Anonymous Student",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Graphite
                    )
                    if (result.profile?.is_verified == 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Badge",
                            tint = activeAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = result.username ?: "@anonymous",
                    fontSize = 13.sp,
                    color = activeAccent,
                    fontWeight = FontWeight.SemiBold
                )

                if (result.profile != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${result.profile.college} • Hostel: ${result.profile.hostel.uppercase()}",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                when (result.requestStatus) {
                    "none" -> {
                        LiquidGlassButton(
                            text = if (isConnecting) "Connecting..." else "Connect",
                            onClick = onConnectClick,
                            enabled = !isConnecting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    "pending" -> {
                        val isMeSender = result.requestSender == HomigoRepository.currentUser.value?.id
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.06f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isMeSender) "Pending Approval" else "Respond in Requests",
                                fontSize = 12.sp,
                                color = Graphite.copy(alpha = 0.5f)
                            )
                        }
                    }
                    "accepted" -> {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = activeAccent.copy(alpha = 0.12f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Connected", color = activeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "rejected" -> {
                        Text("Declined", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedButton(
                onClick = onClickProfile,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = activeAccent),
                modifier = Modifier.weight(1f)
            ) {
                Text("View Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchCard(
    match: Match,
    onClick: () -> Unit,
    onRequestSent: () -> Unit,
    gender: String,
    completion: Int,
    activeAccent: Color
) {
    val coroutineScope = rememberCoroutineScope()
    var isSendingRequest by remember { mutableStateOf(false) }
    var requestError by remember { mutableStateOf<String?>(null) }

    val user = match.user

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        density = GlassDensity.MEDIUM,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName ?: user.name ?: "Anonymous Student",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Graphite
                    )
                    if (user.is_verified == 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(activeAccent.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Verified", color = activeAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hostel: ${user.hostel.uppercase()} | Preferred: ${user.room_preference}",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }

            CompatibilityRing(
                score = match.overallScore,
                avatarEmoji = if (user.gender == "female") "👩" else "👨",
                modifier = Modifier.size(54.dp),
                gender = gender,
                completion = completion
            )
        }

        if (!user.bio.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = user.bio,
                fontSize = 13.sp,
                color = Graphite.copy(alpha = 0.8f),
                maxLines = 2
            )
        }

        if (user.fake_risk_score >= 0.5) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Fake risk",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Unverified Profile (High Risk Score: ${Math.round(user.fake_risk_score * 100)}%)",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val sleepLabel = user.sleep_schedule.replace("_", " ").replaceFirstChar { it.uppercase() }
            LiquidGlassChip(text = sleepLabel)
            LiquidGlassChip(text = "Cleanliness: ${user.cleanliness.replaceFirstChar { it.uppercase() }}")
            LiquidGlassChip(text = "Food: ${user.food_preference.replaceFirstChar { it.uppercase() }}")
            if (user.smoking == "yes") LiquidGlassChip(text = "Smokes")
            if (user.drinking == "yes") LiquidGlassChip(text = "Drinks")
        }

        if (requestError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(requestError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget: ₹${user.budget_min} - ₹${user.budget_max}/mo",
                fontSize = 12.sp,
                color = Graphite.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )

            when (match.requestStatus) {
                "none" -> {
                    LiquidGlassButton(
                        text = if (isSendingRequest) "Connecting..." else "Send Request",
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
                    )
                }
                "pending" -> {
                    val isMeSender = match.requestSender == HomigoRepository.currentUser.value?.id
                    Button(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.06f))
                    ) {
                        Text(
                            text = if (isMeSender) "Pending Approval" else "Respond in Requests",
                            fontSize = 12.sp,
                            color = Graphite.copy(alpha = 0.5f)
                        )
                    }
                }
                "accepted" -> {
                    Button(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = activeAccent.copy(alpha = 0.12f))
                    ) {
                        Text("Mates Connected", color = activeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                "rejected" -> {
                    Text("Declined", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CompatibilityDetailsDialog(
    match: Match,
    onDismiss: () -> Unit,
    gender: String,
    completion: Int,
    activeAccent: Color
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            density = GlassDensity.HIGH
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AI Compatibility Breakdown",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Graphite
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompatibilityRing(
                        score = match.overallScore,
                        avatarEmoji = if (match.user.gender == "female") "👩" else "👨",
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 7.dp,
                        gender = gender,
                        completion = completion
                    )
                    Column {
                        Text("Overall Compatibility", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Graphite)
                        Text("Calculated from 5 lifestyle factors.", fontSize = 11.sp, color = SecondaryText)
                    }
                }

                HorizontalDivider(color = Color.Black.copy(alpha = 0.06f))

                val breakdown = match.breakdown
                val categories = listOf(
                    Triple("Hostel & Budget Match", breakdown.budget, "20% Weight"),
                    Triple("Sleep Schedule sync", breakdown.sleep, "20% Weight"),
                    Triple("Cleanliness preferences", breakdown.cleanliness, "20% Weight"),
                    Triple("Smoking/Drinking status", breakdown.lifestyle, "20% Weight"),
                    Triple("Social preferences", breakdown.social, "20% Weight")
                )

                categories.forEach { (label, score, weight) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Graphite)
                            Text("$score%", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = activeAccent)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = activeAccent,
                            trackColor = activeAccent.copy(alpha = 0.08f)
                        )
                        Text(
                            text = weight,
                            fontSize = 9.sp,
                            color = SecondaryText
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Dismiss", fontWeight = FontWeight.Bold, color = activeAccent)
                }
            }
        }
    }
}
