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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundStartColor, Color.White, Color.White)
                )
            )
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ─── SECTION 1: PREMIUM HEADER ─────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = greeting,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dashboard?.userName ?: "Avika Malhotra",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = accentTextColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "${dashboard?.college ?: "LPU"} • ${dashboard?.hostel ?: "Girls Hostel 5"}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val verified = (dashboard?.profileCompletion ?: 0) == 100
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (verified) Color(0xFF10B981).copy(0.12f) else Color(0xFFF59E0B).copy(0.12f)
                                    ) {
                                        Text(
                                            text = if (verified) "Verified ✓" else "Pending Verification",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (verified) Color(0xFF059669) else Color(0xFFD97706),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Notification button with badge
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White, CircleShape)
                                        .shadow(1.dp, CircleShape)
                                ) {
                                    val count = dashboard?.newRequestsCount ?: 0
                                    if (count > 0) {
                                        BadgedBox(
                                            badge = { Badge(containerColor = primaryColor) { Text("$count") } }
                                        ) {
                                            Icon(Icons.Default.Notifications, "Notifications", tint = Color(0xFF475569))
                                        }
                                    } else {
                                        Icon(Icons.Default.Notifications, "Notifications", tint = Color(0xFF475569))
                                    }
                                }
                                
                                // Profile/Avatar button
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(accentBgColor, CircleShape)
                                        .border(BorderStroke(2.dp, primaryColor), CircleShape)
                                        .shadow(1.dp, CircleShape)
                                ) {
                                    Text(
                                        text = if (isFemale) "👩‍🎓" else "👨‍🎓",
                                        fontSize = 22.sp
                                    )
                                }

                                // Settings button
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White, CircleShape)
                                        .shadow(1.dp, CircleShape)
                                ) {
                                    Icon(Icons.Default.Settings, "Settings", tint = Color(0xFF475569))
                                }
                            }
                        }
                    }
                }

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
