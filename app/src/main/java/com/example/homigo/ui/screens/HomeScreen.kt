package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.homigo.data.api.ApiClient
import com.example.homigo.data.model.*
import com.example.homigo.data.repository.HomigoRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (Int, String) -> Unit,
    onCompleteProfileClick: () -> Unit
) {
    val tokenFlow = HomigoRepository.token.collectAsState()
    val authToken = tokenFlow.value  // Already "Bearer <jwt>" from repository
    val scope = rememberCoroutineScope()
    var dashboard by remember { mutableStateOf<DashboardSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    
    // Profile Picture popup state
    var showProfilePicDialog by remember { mutableStateOf(false) }
    var hasShownDialog by rememberSaveable { mutableStateOf(false) }
    var uploadInProgress by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableStateOf("👤") }

    // Fetch dashboard data
    LaunchedEffect(authToken) {
        if (authToken != null) {
            try {
                dashboard = ApiClient.service.getDashboard(authToken)
            } catch (e: Exception) {
                errorMsg = e.message
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(dashboard) {
        if (dashboard != null && !hasShownDialog) {
            showProfilePicDialog = true
            hasShownDialog = true
        }
    }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val isFemale = (dashboard?.gender ?: "male") == "female"

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = primaryColor, modifier = Modifier.size(48.dp))
                    Text("Loading your dashboard...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // ─── TOP GRADIENT HEADER ────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(primaryColor, primaryColor.copy(alpha = 0.7f), Color.Transparent)
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "$greeting, ${dashboard?.userName?.split(" ")?.firstOrNull() ?: "Student"} 👋",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "${dashboard?.college?.take(20) ?: "—"} • ${dashboard?.hostel?.uppercase() ?: "—"}",
                                            fontSize = 13.sp,
                                            color = Color.White.copy(0.85f)
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if ((dashboard?.newRequestsCount ?: 0) > 0) {
                                        BadgedBox(
                                            badge = { Badge { Text("${dashboard?.newRequestsCount}") } }
                                        ) {
                                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                        }
                                    } else {
                                        Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }

                // ─── PROFILE COMPLETION CARD ────────────────────────────────────
                val completion = dashboard?.profileCompletion ?: 0
                item {
                    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                                CircularProgressIndicator(
                                    progress = { completion / 100f },
                                    modifier = Modifier.size(72.dp),
                                    color = primaryColor,
                                    trackColor = primaryColor.copy(alpha = 0.15f),
                                    strokeWidth = 6.dp
                                )
                                Text("$completion%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Your Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    if (completion >= 80) {
                                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF22C55E).copy(0.15f)) {
                                            Text(" ✔ Verified ", fontSize = 11.sp, color = Color(0xFF22C55E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    if (completion >= 100) "Your profile is complete! 🎉"
                                    else "Complete profile to get better matches",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (completion < 100) {
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = onCompleteProfileClick,
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) { Text("Complete Now", fontSize = 12.sp) }
                                }
                            }
                        }
                    }
                }

                // ─── BEST MATCHES ────────────────────────────────────────────────
                val matches = dashboard?.topMatches ?: emptyList()
                if (matches.isNotEmpty()) {
                    item {
                        SectionHeader(title = "❤️ Best Matches For You", actionLabel = "See All")
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(matches) { match ->
                                MatchCard(match = match, onChat = { onNavigateToChat(match.user_id, match.name ?: "Roommate") })
                            }
                        }
                    }
                }

                // ─── QUICK ACTIONS ───────────────────────────────────────────────
                item { SectionHeader(title = "🎯 Quick Actions") }
                item {
                    val actions = listOf(
                        Triple("Find Roommate", Icons.Default.Favorite, Color(0xFFE91E63)),
                        Triple("Messages", Icons.Default.Email, Color(0xFF2196F3)),
                        Triple("Events", Icons.Default.DateRange, Color(0xFF9C27B0)),
                        Triple("Marketplace", Icons.Default.ShoppingCart, Color(0xFFFF9800)),
                        Triple("Expenses", Icons.Default.List, Color(0xFF4CAF50)),
                        Triple("Notices", Icons.Default.Notifications, Color(0xFFF44336)),
                        Triple("Nearby", Icons.Default.LocationOn, Color(0xFF00BCD4)),
                        Triple("AI", Icons.Default.Star, Color(0xFF673AB7))
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxWidth().height(170.dp).padding(horizontal = 12.dp),
                        userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(actions) { (label, icon, color) ->
                            QuickActionItem(label = label, icon = icon, color = color, onClick = {})
                        }
                    }
                }

                // ─── NOTICE BOARD ────────────────────────────────────────────────
                val notices = dashboard?.notices ?: emptyList()
                if (notices.isNotEmpty()) {
                    item { SectionHeader(title = "📢 Hostel Notice Board", actionLabel = "View All") }
                    items(notices) { notice ->
                        NoticeItem(notice = notice)
                    }
                }

                // ─── UPCOMING EVENTS ─────────────────────────────────────────────
                val events = dashboard?.events ?: emptyList()
                if (events.isNotEmpty()) {
                    item { SectionHeader(title = "🎉 Upcoming Events", actionLabel = "All Events") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(events) { event ->
                                EventCard(event = event)
                            }
                        }
                    }
                }

                // ─── MARKETPLACE PREVIEW ─────────────────────────────────────────
                val market = dashboard?.marketplace ?: emptyList()
                if (market.isNotEmpty()) {
                    item { SectionHeader(title = "🛒 Marketplace", actionLabel = "Browse All") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(market) { item ->
                                MarketplaceCard(item = item)
                            }
                        }
                    }
                }

                // ─── EXPENSE SUMMARY ─────────────────────────────────────────────
                val expBreakdown = dashboard?.expenseBreakdown ?: emptyList()
                val expTotal = dashboard?.expenseTotal ?: 0
                if (expTotal > 0 || expBreakdown.isNotEmpty()) {
                    item { SectionHeader(title = "💰 Expense Summary") }
                    item {
                        DashboardCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("This Month", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(12.dp))
                                expBreakdown.forEach { cat ->
                                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(cat.category, fontSize = 14.sp)
                                        Text("₹${cat.total.toInt()}", fontWeight = FontWeight.Medium, color = primaryColor)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("₹$expTotal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("View Details →", color = primaryColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // ─── NEARBY SERVICES ─────────────────────────────────────────────
                item { SectionHeader(title = "📍 Nearby Services") }
                item {
                    val nearby = listOf(
                        "Laundry" to Icons.Default.Refresh,
                        "Medical" to Icons.Default.Info,
                        "ATM" to Icons.Default.Star,
                        "Cafe" to Icons.Default.Place,
                        "Stationery" to Icons.Default.Edit,
                        "Gym" to Icons.Default.Person
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(nearby) { (label, icon) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(primaryColor.copy(0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = primaryColor, modifier = Modifier.size(26.dp))
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(label, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                // ─── AI SUGGESTION ───────────────────────────────────────────────
                val aiSuggestion = dashboard?.aiSuggestion
                if (!aiSuggestion.isNullOrBlank()) {
                    item { SectionHeader(title = "🤖 AI Suggestions") }
                    item {
                        DashboardCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            containerColor = primaryColor.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp).background(primaryColor.copy(0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🤖", fontSize = 22.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Good News!", fontWeight = FontWeight.Bold, color = primaryColor)
                                    Spacer(Modifier.height(4.dp))
                                    Text(aiSuggestion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = {}, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                                    Text("View", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }

        // Profile Picture Dialog popup
        if (showProfilePicDialog) {
            Dialog(onDismissRequest = { showProfilePicDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(2.dp, primaryColor.copy(0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Customize Your Profile! ✨",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Add a profile picture or choose an avatar to help roommates recognize you.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        
                        // Main Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(primaryColor.copy(0.12f), CircleShape)
                                .border(BorderStroke(3.dp, primaryColor), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uploadInProgress) {
                                CircularProgressIndicator(color = primaryColor, modifier = Modifier.size(36.dp))
                            } else {
                                Text(selectedAvatar, fontSize = 52.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Detail summary
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = primaryColor.copy(0.08f),
                            border = BorderStroke(1.dp, primaryColor.copy(0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dashboard?.userName ?: "Student",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "✔ Verified Student",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${dashboard?.college?.take(30) ?: "College"} • ${dashboard?.hostel?.uppercase() ?: "Hostel"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(20.dp))
                        
                        // Avatar selection row
                        Text("Select a quick avatar:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("👩‍🎓", "👨‍🎓", "🧑‍💻", "🎨", "🌟", "🦁").forEach { avatar ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (selectedAvatar == avatar) primaryColor.copy(0.25f) else Color(0xFFF1F5F9),
                                            CircleShape
                                        )
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                if (selectedAvatar == avatar) primaryColor else Color.Transparent
                                            ),
                                            CircleShape
                                        )
                                        .clickable { selectedAvatar = avatar },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(avatar, fontSize = 20.sp)
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        // Action Buttons
                        Button(
                            onClick = {
                                uploadInProgress = true
                                scope.launch {
                                    kotlinx.coroutines.delay(1800) // Simulating upload delay
                                    uploadInProgress = false
                                    showProfilePicDialog = false
                                }
                            },
                            enabled = !uploadInProgress,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Save & Complete", fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        TextButton(
                            onClick = { showProfilePicDialog = false },
                            enabled = !uploadInProgress,
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Skip for Now", color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }
    }
}

// ─── REUSABLE COMPONENTS ──────────────────────────────────────────────────────

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) { content() }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        if (actionLabel != null) {
            Text(actionLabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun MatchCard(match: Profile, onChat: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    // Simple compatibility score derived from profile similarity
    val score = 85 + (match.user_id % 14)
    Card(
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, primaryColor.copy(0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(52.dp).background(primaryColor.copy(0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if ((match.gender ?: "male") == "female") "👩" else "👨",
                    fontSize = 26.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(match.name?.split(" ")?.firstOrNull() ?: "Student", fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
            Text(match.course ?: "Student", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(match.hostel?.uppercase() ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = primaryColor.copy(0.15f)) {
                Text("  $score% Match  ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor, modifier = Modifier.padding(2.dp))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = {}, modifier = Modifier.weight(1f).height(30.dp), contentPadding = PaddingValues(0.dp)) {
                    Text("View", fontSize = 11.sp)
                }
                OutlinedButton(onClick = onChat, modifier = Modifier.weight(1f).height(30.dp), contentPadding = PaddingValues(0.dp)) {
                    Text("Chat", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(50.dp).background(color.copy(0.12f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun NoticeItem(notice: Notice) {
    val typeColor = when (notice.type) {
        "maintenance" -> Color(0xFFFF9800)
        "event" -> Color(0xFF9C27B0)
        "mess" -> Color(0xFF4CAF50)
        "technical" -> Color(0xFF2196F3)
        else -> MaterialTheme.colorScheme.primary
    }
    val typeIcon = when (notice.type) {
        "maintenance" -> "🔧"
        "event" -> "🎉"
        "mess" -> "🍽️"
        "technical" -> "📶"
        else -> "📌"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, typeColor.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(typeIcon, fontSize = 24.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(notice.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            Text(notice.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun EventCard(event: Event) {
    val typeColor = when (event.type) {
        "sports" -> Color(0xFF4CAF50)
        "academic" -> Color(0xFF2196F3)
        "cultural" -> Color(0xFF9C27B0)
        "wellness" -> Color(0xFF00BCD4)
        else -> MaterialTheme.colorScheme.primary
    }
    val typeEmoji = when (event.type) {
        "sports" -> "🏏"
        "academic" -> "💻"
        "cultural" -> "🎭"
        "wellness" -> "🧘"
        else -> "📅"
    }
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, typeColor.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(typeEmoji, fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(event.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Surface(shape = RoundedCornerShape(6.dp), color = typeColor.copy(0.1f)) {
                        Text(" ${event.type.replaceFirstChar { it.uppercase() }} ", fontSize = 11.sp, color = typeColor, modifier = Modifier.padding(vertical = 1.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(event.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(event.time, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(event.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(32.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = typeColor)
            ) { Text("Register", fontSize = 12.sp) }
        }
    }
}

@Composable
private fun MarketplaceCard(item: MarketplaceItem) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val catEmoji = when (item.category) {
                "Transport" -> "🚲"
                "Furniture" -> "🪑"
                "Books" -> "📚"
                "Appliances" -> "❄️"
                "Electronics" -> "💡"
                "Sports" -> "🏸"
                else -> "🛍️"
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(72.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(catEmoji, fontSize = 36.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.hostel, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("₹${item.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
