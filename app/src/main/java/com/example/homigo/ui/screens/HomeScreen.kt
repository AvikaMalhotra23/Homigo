package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.TextHandleMove

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (Int, String) -> Unit,
    onCompleteProfileClick: () -> Unit
) {
    val tokenFlow = HomigoRepository.token.collectAsState()
    val authToken = tokenFlow.value
    var dashboard by remember { mutableStateOf<DashboardSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

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

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val isFemale = (dashboard?.gender ?: "male") == "female"
    
    // Dynamic Premium Startup Color Palette based on Gender
    val primaryColor = if (isFemale) Color(0xFFEC4899) else Color(0xFF0284C7) // Pink / Sky Blue
    val secondaryColor = if (isFemale) Color(0xFFF472B6) else Color(0xFF38BDF8)
    val backgroundStartColor = if (isFemale) Color(0xFFFFF1F2) else Color(0xFFF0F9FF) // Lightest gradient bg
    val accentBgColor = if (isFemale) Color(0xFFFFF0F6) else Color(0xFFF0F7FF)
    val accentTextColor = if (isFemale) Color(0xFFDB2777) else Color(0xFF0369A1)
    val cardBgColor = Color.White

    val listState = rememberLazyListState()
    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val headerElevation by animateDpAsState(
        targetValue = if (isScrolled) 4.dp else 0.dp,
        label = "headerElevation"
    )

    val haptic = LocalHapticFeedback.current
    var isFabExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(backgroundStartColor, Color.White, Color.White)
                    )
                )
        ) {
            // Sticky Premium Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = headerElevation
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greeting.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937).copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = dashboard?.userName ?: "Avika Malhotra",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1F2937)
                            )
                            val verified = (dashboard?.profileCompletion ?: 0) == 100
                            if (verified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${dashboard?.college ?: "LPU"} • ${dashboard?.hostel ?: "Girls Hostel 5"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1F2937).copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF3F4F6), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            val count = dashboard?.newRequestsCount ?: 0
                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-2).dp, y = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF3F4F6), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFE5E7EB), CircleShape)
                                .border(BorderStroke(2.dp, Color(0xFF38BDF8)), CircleShape)
                        ) {
                            Text(
                                text = if (isFemale) "👩‍🎓" else "👨‍🎓",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = primaryColor, modifier = Modifier.size(48.dp))
                            Text("Loading Homigo Dashboard...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // ─── SECTION 2: PROFILE COMPLETION & READINESS CARD ────────────
                        val completion = dashboard?.profileCompletion ?: 0
                        if (completion < 100) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Profile Completion",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF1E293B)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Complete profile to receive better roommate suggestions.",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                text = "$completion%",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 20.sp,
                                                color = primaryColor
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        // Progress bar
                                        LinearProgressIndicator(
                                            progress = { completion / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp)),
                                            color = primaryColor,
                                            trackColor = primaryColor.copy(alpha = 0.12f)
                                        )
                                        
                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Checklist of missing features
                                        Text(
                                            text = "Profile Checklist:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF475569)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            ChecklistItem(label = "Bio", checked = true)
                                            ChecklistItem(label = "Lifestyle", checked = completion >= 80)
                                            ChecklistItem(label = "Interests", checked = completion >= 90)
                                        }

                                        Spacer(modifier = Modifier.height(18.dp))

                                        Button(
                                            onClick = onCompleteProfileClick,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                            modifier = Modifier.fillMaxWidth().height(46.dp)
                                        ) {
                                            Text("Complete Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // ─── SECTION 3: ROOMMATE DISCOVERY CARD ────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Find Compatible Roommates",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Start discovering students from ${dashboard?.college ?: "LPU"} • ${dashboard?.hostel ?: "GH-5"}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))

                                    val candidates = dashboard?.topMatches ?: emptyList()
                                    if (candidates.isEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFF8FAFC),
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                        ) {
                                            Text(
                                                text = "No students have registered yet. We'll notify you when students from your university join.",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B),
                                                modifier = Modifier.padding(14.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "${candidates.size} Compatible Students Found",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {},
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                        modifier = Modifier.fillMaxWidth().height(46.dp)
                                    ) {
                                        Text("Discover Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        // ─── SECTION 4: MY REQUESTS SUMMARY ────────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "My Roommate Requests",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RequestCounter(label = "Incoming", count = 0, color = primaryColor)
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(40.dp)
                                                .background(Color(0xFFE2E8F0))
                                        )
                                        RequestCounter(label = "Sent", count = dashboard?.newRequestsCount ?: 1, color = Color(0xFF22C55E))
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(40.dp)
                                                .background(Color(0xFFE2E8F0))
                                        )
                                        RequestCounter(label = "Accepted", count = 0, color = Color(0xFF3B82F6))
                                    }
                                }
                            }
                        }

                        // ─── SECTION 5: COMPATIBILITY PROFILE ──────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "Your Compatibility Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Your values, used to compare with potential roommates.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    
                                    val scores = listOf(
                                        Triple("Lifestyle", 90, primaryColor),
                                        Triple("Study Habits", 80, secondaryColor),
                                        Triple("Cleanliness", 95, Color(0xFF10B981)),
                                        Triple("Budget", 85, Color(0xFFF59E0B)),
                                        Triple("Communication", 90, Color(0xFF6366F1))
                                    )
                                    
                                    scores.forEach { (label, value, color) ->
                                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                                                Text("$value%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = { value / 100f },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = color,
                                                trackColor = color.copy(alpha = 0.1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ─── SECTION 6: QUICK ACTIONS ──────────────────────────────────
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Text(
                                    text = "Quick Actions",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val quickActions = listOf(
                                    Triple("Edit Profile", Icons.Default.Edit, primaryColor),
                                    Triple("Discover", Icons.Default.Search, Color(0xFF3B82F6)),
                                    Triple("Messages", Icons.Default.Email, Color(0xFF10B981)),
                                    Triple("Verification", Icons.Default.CheckCircle, Color(0xFFF59E0B)),
                                    Triple("Compatibility", Icons.Default.Favorite, Color(0xFFEC4899)),
                                    Triple("Support", Icons.Default.Info, Color(0xFF6366F1))
                                )
                                
                                // Render 3 columns
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        QuickActionCard(action = quickActions[0])
                                        QuickActionCard(action = quickActions[1])
                                    }
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        QuickActionCard(action = quickActions[2])
                                        QuickActionCard(action = quickActions[3])
                                    }
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        QuickActionCard(action = quickActions[4])
                                        QuickActionCard(action = quickActions[5])
                                    }
                                }
                            }
                        }

                        // ─── SECTION 7: UNIVERSITY ANNOUNCEMENTS ───────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🏛️", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "University Notices",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No announcements available.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // ─── SECTION 8: HOSTEL ANNOUNCEMENTS ───────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🏢", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Hostel Notices",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No notices available.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // ─── SECTION 9: EMERGENCY SAFETY CENTER ────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)), // Soft red Safety Center background
                                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🚨", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Safety Center",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Quick access to verified emergency contacts.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF7F1D1D).copy(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    val emergencies = listOf(
                                        "Hostel Warden" to "📞 911-XXXX-XXX",
                                        "Security Office" to "📞 100-XXXX-XXX",
                                        "Medical Room" to "📞 108-XXXX-XXX",
                                        "Women Helpline" to "📞 1091",
                                        "Police Control" to "📞 100"
                                    )

                                    emergencies.forEach { (label, contact) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF991B1B))
                                            Text(contact, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7F1D1D))
                                        }
                                    }
                                }
                            }
                        }

                        // ─── SECTION 10: WHAT'S NEW & FOOTER ───────────────────────────
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "What's New in Homigo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• Version 1.0 Live Release\n• Added detailed interests & lifestyle matching\n• Added university safety center contacts",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    text = "Homigo",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Version 1.0 • Privacy Policy • Terms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ─── SCRIM / GLASSMORPHIC OVERLAY ───
        AnimatedVisibility(
            visible = isFabExpanded,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isFabExpanded = false
                    }
            )
        }

        // ─── SPEED DIAL MENU AND FAB CONTAINER ───
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Actions (Find Roommate, Post Vacancy, Ask AI, etc.)
                val speedDialActions = listOf(
                    Triple("Find Roommate", "👥", Color(0xFF3B82F6)),
                    Triple("Post Room Vacancy", "🏠", Color(0xFF10B981)),
                    Triple("Ask Homigo AI", "🤖", Color(0xFF8B5CF6)),
                    Triple("Find Nearby Rooms", "📍", Color(0xFFF59E0B)),
                    Triple("Create Room Request", "📝", Color(0xFFEC4899))
                )

                speedDialActions.forEachIndexed { index, (label, emoji, color) ->
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn(animationSpec = tween(150 + index * 50)) +
                                scaleIn(initialScale = 0.8f, animationSpec = tween(150 + index * 50)) +
                                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(150 + index * 50)),
                        exit = fadeOut(animationSpec = tween(150)) +
                                scaleOut(targetScale = 0.8f, animationSpec = tween(150)) +
                                slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(150))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true, color = color.copy(alpha = 0.15f))
                                ) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    isFabExpanded = false
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            // Label
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            // Icon circle
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color.White, CircleShape)
                                    .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), CircleShape)
                                    .shadow(2.dp, CircleShape)
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                // Main FAB
                val gradientBrush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF06B6D4)) // Blue to Cyan gradient
                )
                val rotation by animateFloatAsState(
                    targetValue = if (isFabExpanded) 135f else 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "fabRotation"
                )

                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(28.dp), clip = false)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isFabExpanded = !isFabExpanded
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(gradientBrush)
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer(rotationZ = rotation)
                            )
                            Text(
                                text = "Find Match",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── HELPER COMPONENTS ─────────────────────────────────────────────────────────

@Composable
private fun ChecklistItem(label: String, checked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null,
            tint = if (checked) Color(0xFF10B981) else Color(0xFFCBD5E1),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (checked) Color(0xFF475569) else Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun RequestCounter(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "$count",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun QuickActionCard(action: Triple<String, ImageVector, Color>) {
    val (label, icon, color) = action
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
