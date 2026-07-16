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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    val currentUserState = HomigoRepository.currentUser.collectAsState()
    val user = currentUserState.value
    var promptedUsername by rememberSaveable { mutableStateOf(false) }
    var showUsernameSetupDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null && user.username.isNullOrEmpty() && !promptedUsername) {
            promptedUsername = true
            showUsernameSetupDialog = true
        }
    }

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
            // Liquid Glass Welcome Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.55f))
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0.15f))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // personalized greeting
                            Text(
                                text = "$greeting, ${myProfile?.name?.substringBefore(" ") ?: dashboard?.userName ?: "Harshit"} 👋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                letterSpacing = (-0.5).sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Badge & Subtitle details
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (myProfile?.is_verified == 1) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF2563EB).copy(alpha = 0.1f))
                                            .border(0.5.dp, Color(0xFF2563EB).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "Verified student",
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Verified Student",
                                                color = Color(0xFF2563EB),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE2E8F0))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Student",
                                            color = Color(0xFF475569),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Text(
                                    text = myProfile?.college ?: "University",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Hostel, Course, Year info chip details
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val hostelVal = myProfile?.hostel?.uppercase() ?: "Not Assigned"
                                val courseVal = myProfile?.course ?: "N/A"
                                val yearVal = myProfile?.year ?: "N/A"
                                
                                InfoTag(label = "Hostel $hostelVal")
                                InfoTag(label = courseVal)
                                InfoTag(label = "$yearVal Year")
                            }
                        }

                        // Notification Trigger
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                                .border(1.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                                .shadow(elevation = 2.dp, shape = CircleShape)
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
                    // ─── 1. STUDENT STATUS CARD ───
                    item {
                        Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp)) {
                            StudentStatusCard(
                                completion = completion,
                                isVerified = myProfile?.is_verified == 1,
                                pendingRequestsCount = requestsResponse?.incoming?.size ?: 0,
                                activeAccent = activeAccent,
                                onCompleteProfileClick = onCompleteProfileClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // ─── 2. MY ROOMMATE JOURNEY CARD ───
                    item {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            RoommateJourneyCard(
                                completion = completion,
                                isVerified = myProfile?.is_verified == 1,
                                hasChats = chatList.isNotEmpty(),
                                hasMatches = (dashboard?.topMatches?.isNotEmpty() == true),
                                onNavigateToDiscover = onNavigateToDiscover,
                                onCompleteProfileClick = onCompleteProfileClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // ─── 3. COMPATIBILITY PREFERENCES CARD ───
                    item {
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            CompatibilityPreferencesCard(
                                profile = myProfile,
                                activeAccent = activeAccent,
                                onClick = onCompleteProfileClick,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // ─── 4. TODAY'S AI BRIEF ───
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
        if (!isFabExpanded) {
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
        }

        // Material 3 modal bottom sheet
        if (isFabExpanded) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val lightAccentBg = if (gender.lowercase() == "female") Color(0xFFFFF1F2) else Color(0xFFF0F9FF)

            ModalBottomSheet(
                onDismissRequest = { isFabExpanded = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = Color.White,
                scrimColor = Color.Black.copy(alpha = 0.25f), // 20–30% black overlay
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                tonalElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🤖", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "Homigo AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Graphite
                            )
                            Text(
                                text = "Your smart roommate assistant",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        }
                    }

                    // Search Field
                    var searchText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Ask Homigo AI...", color = SecondaryText, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search icon",
                                tint = activeAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = activeAccent,
                            unfocusedBorderColor = Border,
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.4f),
                            focusedTextColor = Graphite,
                            unfocusedTextColor = Graphite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // suggestions list
                    val suggestions = listOf(
                        Triple("👥", "Find Compatible Roommates", "Find matches matching your preferences" to onNavigateToDiscover),
                        Triple("🏠", "Find Available Rooms", "View vacant rooms in your hostel" to onNavigateToDiscover),
                        Triple("🎯", "Who Matches Me Best?", "Get a curated list of top matches" to onNavigateToDiscover),
                        Triple("📊", "Explain My Compatibility Score", "Details of compatibility breakdown" to {}),
                        Triple("📝", "Improve My Profile", "Add missing info to boost visibility" to onCompleteProfileClick),
                        Triple("🚩", "Report an Issue", "Notify support about roommate disputes" to onNavigateToReviews)
                    )

                    val filteredSuggestions = suggestions.filter { (_, title, descAndAction) ->
                        title.contains(searchText, ignoreCase = true) ||
                        descAndAction.first.contains(searchText, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredSuggestions) { (icon, title, descAndAction) ->
                            val (desc, action) = descAndAction
                            Card(
                                onClick = {
                                    isFabExpanded = false
                                    action()
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = lightAccentBg
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp) // Minimum touch target is 48dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(icon, fontSize = 20.sp)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Graphite
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = SecondaryText
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowRight,
                                        contentDescription = "Navigate to $title",
                                        tint = activeAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (filteredSuggestions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No suggestions match your query.", color = SecondaryText, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showUsernameSetupDialog) {
            var inputUsername by remember { mutableStateOf("") }
            var availabilityMessage by remember { mutableStateOf("") }
            var isAvailable by remember { mutableStateOf<Boolean?>(null) }
            var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
            var isChecking by remember { mutableStateOf(false) }
            var isSavingUsername by remember { mutableStateOf(false) }

            // Live check availability when typing
            LaunchedEffect(inputUsername) {
                val trimmed = inputUsername.trim()
                if (trimmed.isEmpty()) {
                    availabilityMessage = ""
                    isAvailable = null
                    suggestions = emptyList()
                    return@LaunchedEffect
                }
                
                // Validate format rules
                val clean = if (trimmed.startsWith("@")) trimmed.substring(1) else trimmed
                val regex = "^[a-zA-Z0-9_.]+$".toRegex()
                if (!regex.matches(clean)) {
                    availabilityMessage = "Only letters, numbers, underscores, and dots allowed."
                    isAvailable = false
                    suggestions = emptyList()
                    return@LaunchedEffect
                }
                
                if (clean.length < 1 || clean.length > 30) {
                    availabilityMessage = "Must be between 1 and 30 characters."
                    isAvailable = false
                    suggestions = emptyList()
                    return@LaunchedEffect
                }

                isChecking = true
                try {
                    val checkRes = HomigoRepository.checkUsername(clean)
                    isAvailable = checkRes.available
                    if (checkRes.available) {
                        availabilityMessage = "✓ @$clean is available"
                    } else {
                        availabilityMessage = "✗ Username isn't available."
                        suggestions = checkRes.suggestions
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isChecking = false
                }
            }

            AlertDialog(
                onDismissRequest = { if (!isSavingUsername) showUsernameSetupDialog = false },
                title = { Text("Choose a Username", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Every user needs a unique username to be discovered by others on Homigo.",
                            fontSize = 14.sp,
                            color = SecondaryText
                        )

                        OutlinedTextField(
                            value = inputUsername,
                            onValueChange = { inputUsername = it },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. harshit_garg") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = isAvailable == false
                        )

                        if (isChecking) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp).align(Alignment.CenterHorizontally))
                        } else if (availabilityMessage.isNotEmpty()) {
                            Text(
                                text = availabilityMessage,
                                color = if (isAvailable == true) Color(0xFF22C55E) else MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Suggestions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Graphite)
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                suggestions.forEach { sug ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                            .clickable { inputUsername = sug }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(sug, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isSavingUsername = true
                            coroutineScope.launch {
                                try {
                                    val clean = if (inputUsername.startsWith("@")) inputUsername.substring(1) else inputUsername
                                    HomigoRepository.updateUsername(clean)
                                    showUsernameSetupDialog = false
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    isSavingUsername = false
                                }
                            }
                        },
                        enabled = isAvailable == true && !isSavingUsername
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUsernameSetupDialog = false },
                        enabled = !isSavingUsername
                    ) {
                        Text("Cancel")
                    }
                }
            )
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

@Composable
fun InfoTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.04f))
            .border(0.5.dp, Color.Black.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF475569)
        )
    }
}

@Composable
fun StudentStatusCard(
    completion: Int,
    isVerified: Boolean,
    pendingRequestsCount: Int,
    activeAccent: Color,
    onCompleteProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val readinessScore = remember(completion) {
        ((completion * 0.95).toInt() + 5).coerceIn(5, 99)
    }

    LiquidGlassCard(
        modifier = modifier,
        density = GlassDensity.HIGH
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛡️", fontSize = 20.sp)
                    Text(
                        text = "STUDENT STATUS",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                }

                if (completion < 100) {
                    Text(
                        text = "Complete Profile →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeAccent,
                        modifier = Modifier.clickable { onCompleteProfileClick() }
                    )
                }
            }

            // 2x2 Clean Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Metric 1: Profile Completion
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Small circular progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(36.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = completion / 100f,
                                strokeWidth = 3.dp,
                                color = activeAccent,
                                trackColor = Color.Black.copy(alpha = 0.05f)
                            )
                            Text(
                                text = "$completion%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                        Column {
                            Text(
                                text = "Profile Setup",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = if (completion >= 100) "Completed" else "In Progress",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                // Metric 2: Verification Status
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isVerified) Color(0xFF22C55E).copy(alpha = 0.1f) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVerified) Icons.Filled.CheckCircle else Icons.Filled.Info,
                                contentDescription = null,
                                tint = if (isVerified) Color(0xFF22C55E) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Verification",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = if (isVerified) "Verified" else "Pending",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVerified) Color(0xFF15803D) else Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Metric 3: Roommate Readiness Score
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$readinessScore",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0284C7)
                            )
                        }
                        Column {
                            Text(
                                text = "Readiness",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Score",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                // Metric 4: Pending Requests
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (pendingRequestsCount > 0) Color(0xFFEF4444).copy(alpha = 0.1f) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$pendingRequestsCount",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = if (pendingRequestsCount > 0) Color(0xFFEF4444) else Color(0xFF64748B)
                            )
                        }
                        Column {
                            Text(
                                text = "Pending",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = if (pendingRequestsCount == 1) "1 Request" else "$pendingRequestsCount Requests",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoommateJourneyCard(
    completion: Int,
    isVerified: Boolean,
    hasChats: Boolean,
    hasMatches: Boolean,
    onNavigateToDiscover: () -> Unit,
    onCompleteProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine the active step (1-indexed: 1 to 6)
    val activeStep = when {
        completion < 100 -> 2 // Step 2: Complete Preferences
        !isVerified -> 3 // Step 3: Verification
        !hasChats && !hasMatches -> 4 // Step 4: Waiting for Compatible Students
        !hasChats -> 5 // Step 5: Chat
        else -> 6 // Step 6: Become Roommates
    }

    val steps = listOf(
        "Create Profile" to "Account created successfully",
        "Complete Preferences" to "Fill lifestyle & interest details",
        "Verification" to "Verify student ID proof",
        "Waiting for compatible students" to "AI finding roommate matches",
        "Chat" to "Connect with compatible matches",
        "Become Roommates" to "Confirm room & move in together"
    )

    LiquidGlassCard(
        modifier = modifier,
        density = GlassDensity.HIGH
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✨", fontSize = 20.sp)
                    Text(
                        text = "MY ROOMMATE JOURNEY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                }

                // CTA action depending on status
                if (activeStep == 2) {
                    Text(
                        text = "Finish Setup →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable { onCompleteProfileClick() }
                    )
                } else if (activeStep >= 4) {
                    Text(
                        text = "Find Matches →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable { onNavigateToDiscover() }
                    )
                }
            }

            // Vertical Timeline
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                steps.forEachIndexed { index, (title, desc) ->
                    val stepNum = index + 1
                    val isCompleted = stepNum < activeStep
                    val isActive = stepNum == activeStep
                    val isMuted = stepNum > activeStep

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Timeline Left Indicator (Circle and Line)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> Color(0xFF22C55E)
                                            isActive -> Color(0xFF2563EB)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = when {
                                            isActive -> Color(0xFF93C5FD)
                                            else -> Color.White
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$stepNum",
                                        color = if (isActive) Color.White else Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Vertical connecting line (only if not the last item)
                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(
                                            if (isCompleted) Color(0xFF22C55E).copy(alpha = 0.5f)
                                            else Color(0xFFE2E8F0)
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Step Text Info
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                                color = when {
                                    isActive -> Color(0xFF0F172A)
                                    isCompleted -> Color(0xFF334155)
                                    else -> Color(0xFF94A3B8)
                                }
                            )
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = when {
                                    isActive -> Color(0xFF475569)
                                    else -> Color(0xFF94A3B8)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompatibilityPreferencesCard(
    profile: Profile?,
    activeAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tags = remember(profile) {
        val list = mutableListOf<String>()
        if (profile != null) {
            // Sleep Schedule
            when (profile.sleep_schedule.lowercase()) {
                "early_bird" -> list.add("Early Sleeper")
                "night_owl" -> list.add("Night Owl")
                else -> list.add("Flexible Sleeper")
            }
            // Food
            when (profile.food_preference.lowercase()) {
                "veg" -> list.add("Vegetarian")
                "non_veg" -> list.add("Non-Vegetarian")
                else -> list.add("Any Diet")
            }
            // Smoking
            when (profile.smoking.lowercase()) {
                "no" -> list.add("Non-Smoker")
                "occasionally" -> list.add("Occasional Smoker")
                else -> list.add("Smoker")
            }
            // Cleanliness
            when (profile.cleanliness.lowercase()) {
                "high" -> list.add("High Cleanliness")
                "moderate" -> list.add("Moderate Cleanliness")
                else -> list.add("Flexible Cleanliness")
            }
            // Budget
            list.add("Budget: ₹${profile.budget_min}-${profile.budget_max}")
            // Parsing interests
            if (!profile.interests.isNullOrBlank()) {
                try {
                    val array = org.json.JSONArray(profile.interests)
                    for (i in 0 until array.length()) {
                        list.add(array.getString(i))
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
        } else {
            // Default tags if profile not populated yet
            list.addAll(listOf("Early Sleeper", "Vegetarian", "Non-Smoker", "Coding", "Gym", "Budget: ₹4k-6k", "Clean Room", "Morning Study"))
        }
        list
    }

    LiquidGlassCard(
        modifier = modifier.clickable { onClick() },
        density = GlassDensity.HIGH
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚙️", fontSize = 20.sp)
                    Text(
                        text = "COMPATIBILITY PREFERENCES",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        letterSpacing = 0.5.sp
                    )
                }
                
                Text(
                    text = "Edit →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeAccent
                )
            }

            // Glass Chips Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.4f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}

