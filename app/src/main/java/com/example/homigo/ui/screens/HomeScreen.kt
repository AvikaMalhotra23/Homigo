package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.api.ApiClient
import com.example.homigo.data.model.*
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (Int, String) -> Unit,
    onCompleteProfileClick: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToDiscover: () -> Unit
) {
    val tokenFlow = HomigoRepository.token.collectAsState()
    val authToken = tokenFlow.value
    var dashboard by remember { mutableStateOf<DashboardSummary?>(null) }
    var myProfile by remember { mutableStateOf<Profile?>(null) }
    var chatList by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var requestsResponse by remember { mutableStateOf<RequestsResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    var isFabExpanded by remember { mutableStateOf(false) }

    // Fetch dashboard, profile, chats, and requests
    LaunchedEffect(authToken) {
        if (authToken != null) {
            try {
                dashboard = ApiClient.service.getDashboard(authToken)
                myProfile = HomigoRepository.fetchProfile()
                chatList = HomigoRepository.getChatList()
                requestsResponse = HomigoRepository.getRequests()
            } catch (e: Exception) {
                // Handle silently
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val gender = dashboard?.gender ?: "male"
    val completion = dashboard?.profileCompletion ?: 0

    // Dynamic color accents
    val activeAccent = remember(gender, completion) {
        val isCompleted = completion >= 100
        if (!isCompleted) Color(0xFF0284C7)
        else if (gender.lowercase() == "female") Color(0xFFEC4899)
        else Color(0xFF2563EB)
    }

    // Client-side compatibility scorer matching backend logic
    fun getCompatibility(other: Profile): Int {
        val mine = myProfile ?: return 75
        var score = 55
        if (other.sleep_schedule == mine.sleep_schedule) score += 15
        if (other.food_preference == mine.food_preference) score += 10
        if (other.cleanliness == mine.cleanliness) score += 10
        if (other.smoking == mine.smoking) score += 10
        if (other.drinking == mine.drinking) score += 5
        return Math.min(99, score)
    }

    // Client-side tags parser
    fun parseTags(jsonStr: String?): List<String> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        return try {
            val array = org.json.JSONArray(jsonStr)
            List(array.length()) { array.getString(it) }.take(3)
        } catch (e: Exception) {
            emptyList()
        }
    }

    FloatingBackground(gender = gender, completion = completion) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.85f),
                border = BorderStroke(width = 0.5.dp, color = Color.Black.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = activeAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = dashboard?.userName ?: "Harshit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Graphite
                        )
                    }

                    // Notification Trigger
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .clickable { onNavigateToRequests() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = activeAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        if (requestsResponse?.incoming?.isNotEmpty() == true) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(8.dp)
                                    .background(Color.Red, CircleShape)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = activeAccent)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 90.dp), // Clear bottom dock space
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // ─── 1. AI HERO COMMAND CENTER ───
                    item {
                        Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp)) {
                            LiquidGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                density = GlassDensity.HIGH
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🤖", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "HOMIGO AI",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = activeAccent,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "You have ${dashboard?.topMatches?.size ?: 6} new compatible roommates today.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Graphite
                                        )
                                        Text(
                                            text = "92% Average Compatibility",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = SecondaryText,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                LiquidGlassButton(
                                    text = "Discover Matches",
                                    onClick = onNavigateToDiscover,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Inline Onboarding Tracker
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Profile Completed: $completion%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Graphite.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Continue →",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = activeAccent,
                                        modifier = Modifier.clickable { onCompleteProfileClick() }
                                    )
                                }
                            }
                        }
                    }

                    // ─── 2. TODAY'S AI BRIEF ───
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "Today's AI Brief",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Graphite,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            LiquidGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                density = GlassDensity.MEDIUM
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✨", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Daily Campus Summary",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Graphite
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    BulletPoint(text = "4 students joined BH-5 today")
                                    BulletPoint(text = "2 people viewed your profile")
                                    BulletPoint(text = "AI found 3 better roommate matches")
                                    BulletPoint(text = "${requestsResponse?.incoming?.size ?: 1} roommate request is waiting")
                                }
                            }
                        }
                    }

                    // ─── 3. MATCH CAROUSEL ───
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Highly Compatible Roommates",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Graphite,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 10.dp)
                            )

                            val candidates = dashboard?.topMatches ?: emptyList()
                            if (candidates.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "No compatible matches found yet. Try adjusting your preferences.",
                                            fontSize = 13.sp,
                                            color = SecondaryText,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            } else {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(candidates) { other ->
                                        val matchScore = getCompatibility(other)
                                        val tags = parseTags(other.interests)

                                        Box(modifier = Modifier.width(280.dp)) {
                                            LiquidGlassCard(
                                                modifier = Modifier.fillMaxWidth(),
                                                density = GlassDensity.HIGH
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    CompatibilityRing(
                                                        score = matchScore,
                                                        avatarEmoji = if (other.gender == "female") "👩" else "👨",
                                                        modifier = Modifier.size(50.dp),
                                                        gender = gender,
                                                        completion = completion
                                                    )
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = other.name ?: "Roommate",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = Graphite,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = "${other.course} • Hostel ${other.preferred_hostel?.uppercase() ?: ""}",
                                                            fontSize = 11.sp,
                                                            color = SecondaryText,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                // Lifestyle Tags Row
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    tags.take(3).forEach { tag ->
                                                        LiquidGlassChip(text = tag)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(12.dp))

                                                // AI Reasoning
                                                Text(
                                                    text = "🤖 AI Predicts:",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = activeAccent,
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = "Both sleep late (${other.sleep_schedule?.replace("_", " ") ?: "night owl"}) and prefer a quiet library setup.",
                                                    fontSize = 11.sp,
                                                    color = Graphite.copy(alpha = 0.8f),
                                                    lineHeight = 15.sp,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                LiquidGlassButton(
                                                    text = "Connect →",
                                                    onClick = { onNavigateToChat(other.user_id, other.name ?: "Roommate") },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ─── 4. CAMPUS EMOTIONAL PRESENCE ───
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "Campus Activity",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Graphite,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            LiquidGlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                density = GlassDensity.MEDIUM
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("🏠", fontSize = 16.sp)
                                    Text(
                                        text = "${myProfile?.preferred_hostel?.uppercase() ?: "BH-5"} Community",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Graphite
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    PresenceCounter(count = 4, label = "Online Now")
                                    PresenceCounter(count = 8, label = "Nearby")
                                    PresenceCounter(count = 3, label = "Searching")
                                }
                            }
                        }
                    }

                    // ─── 5. CONVERSATIONS ───
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Active Conversations",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Graphite,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 10.dp)
                            )

                            if (chatList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "No active conversations yet.",
                                            fontSize = 13.sp,
                                            color = SecondaryText,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            } else {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(chatList) { roommate ->
                                        Box(
                                            modifier = Modifier
                                                .width(160.dp)
                                                .shadow(2.dp, RoundedCornerShape(20.dp))
                                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                                .clickable { onNavigateToChat(roommate.user_id, roommate.name ?: "Roommate") }
                                                .padding(14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(activeAccent.copy(alpha = 0.08f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(if (roommate.gender == "female") "👩" else "👨", fontSize = 16.sp)
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = roommate.name ?: "Roommate",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Graphite,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Tap to chat",
                                                    fontSize = 10.sp,
                                                    color = SecondaryText
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
        }

        // ─── FLOATING CHATGPT-STYLE AI ACTIONS FAB ───
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(12.dp, CircleShape, spotColor = activeAccent.copy(alpha = 0.4f))
                    .background(activeAccent, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .size(56.dp)
                    .clickable { isFabExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Ask Homigo AI",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // ChatGPT Prompt Overlay Panel
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { isFabExpanded = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {}
                        .padding(20.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        density = GlassDensity.HIGH
                    ) {
                        Text(
                            text = "🤖 Ask Homigo AI",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Graphite
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val prompts = listOf(
                            "Find me a roommate" to onNavigateToDiscover,
                            "Find rooms" to onNavigateToDiscover,
                            "Who matches me?" to onNavigateToDiscover,
                            "Summarize today's activity" to {},
                            "Report issue" to onNavigateToReviews
                        )

                        prompts.forEach { (prompt, action) ->
                            Text(
                                text = prompt,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeAccent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isFabExpanded = false
                                        action()
                                    }
                                    .padding(vertical = 12.dp)
                            )
                            Divider(color = Color.Black.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────
@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .background(Graphite.copy(alpha = 0.6f), CircleShape)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Graphite.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun PresenceCounter(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Graphite
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = SecondaryText,
            fontWeight = FontWeight.Bold
        )
    }
}
