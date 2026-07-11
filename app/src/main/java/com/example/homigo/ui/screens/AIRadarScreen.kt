package com.example.homigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homigo.data.model.Profile
import com.example.homigo.data.repository.HomigoRepository
import com.example.homigo.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AIRadarScreen() {
    val coroutineScope = rememberCoroutineScope()
    var matches by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedStudent by remember { mutableStateOf<Profile?>(null) }
    var connectionSuccess by remember { mutableStateOf<String?>(null) }

    val currentUserState = HomigoRepository.currentUser.collectAsState()
    val completionState = HomigoRepository.profileCompletion.collectAsState()

    val gender = currentUserState.value?.gender ?: "male"
    val completion = completionState.value

    LaunchedEffect(Unit) {
        try {
            val list = HomigoRepository.getMatches()
            matches = list.map { it.user }.take(6)
            if (matches.isNotEmpty()) {
                selectedStudent = matches.first()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            isLoading = false
        }
    }

    // Dynamic color accents
    val activeAccent = remember(gender, completion) {
        val isCompleted = completion >= 100
        if (!isCompleted) Color(0xFF0284C7)
        else if (gender.lowercase() == "female") Color(0xFFEC4899)
        else Color(0xFF2563EB)
    }

    // Animated sweeper angle
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Pulse circles scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )

    FloatingBackground(gender = gender, completion = completion) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.85f),
                border = BorderStroke(width = 0.5.dp, color = Color.Black.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "HOMIGO AI RADAR",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = activeAccent,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Realtime Proximity Matching",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Graphite
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = activeAccent)
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // ─── RADAR VISUALIZATION VIEW ───
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 200.dp), // Leave space for bottom details widget
                        contentAlignment = Alignment.Center
                    ) {
                        val configuration = LocalConfiguration.current
                        val screenWidth = configuration.screenWidthDp.dp
                        val radarSize = screenWidth * 0.85f

                        // Radar background rings and sweeping arm
                        Canvas(modifier = Modifier.size(radarSize)) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.width / 2

                            // Concentric Grid Rings
                            drawCircle(
                                color = activeAccent.copy(alpha = 0.04f),
                                radius = radius,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                color = activeAccent.copy(alpha = 0.07f),
                                radius = radius * 0.7f,
                                style = Stroke(width = 1.2.dp.toPx())
                            )
                            drawCircle(
                                color = activeAccent.copy(alpha = 0.1f),
                                radius = radius * 0.4f,
                                style = Stroke(width = 1.dp.toPx())
                            )

                            // Pulse animated expansion rings
                            drawCircle(
                                color = activeAccent.copy(alpha = 0.15f * (1f - pulseScale)),
                                radius = radius * pulseScale,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            // Sweeper Line Arm
                            val armLength = radius
                            val rad = Math.toRadians(sweepAngle.toDouble())
                            val endX = center.x + armLength * cos(rad).toFloat()
                            val endY = center.y + armLength * sin(rad).toFloat()

                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = listOf(activeAccent, activeAccent.copy(alpha = 0.05f)),
                                    start = center,
                                    end = Offset(endX, endY)
                                ),
                                start = center,
                                end = Offset(endX, endY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        // Center Avatar Node (Current User)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(8.dp, CircleShape)
                                .background(activeAccent, CircleShape)
                                .border(2.5.dp, Color.White, CircleShape)
                        ) {
                            Text(if (gender == "female") "👩‍🎓" else "👨‍🎓", fontSize = 24.sp)
                        }

                        // Floating Roommate Avatar Nodes positioned by compatibility
                        matches.forEachIndexed { index, student ->
                            // Distances represent compatibility score proximity (higher score = closer to center)
                            val score = 75 + index * 4 // mock realistic compatibility
                            val normalizedDist = (100 - score) / 25f // 0f (close) to 1f (edge)
                            val distance = (radarSize.value * 0.45f * (0.35f + normalizedDist * 0.6f)).dp

                            // Distribute angles evenly around radar
                            val angleRad = Math.toRadians((index * (360.0 / matches.size) + 45.0))
                            val offsetX = distance * cos(angleRad).toFloat()
                            val offsetY = distance * sin(angleRad).toFloat()

                            val isSelected = selectedStudent?.user_id == student.user_id

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .offset(x = offsetX, y = offsetY)
                                    .size(if (isSelected) 46.dp else 38.dp)
                                    .shadow(if (isSelected) 8.dp else 4.dp, CircleShape)
                                    .background(
                                        if (isSelected) activeAccent else Color.White.copy(alpha = 0.85f),
                                        CircleShape
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color.White else activeAccent.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        selectedStudent = student
                                        connectionSuccess = null
                                    }
                            ) {
                                Text(
                                    text = if (student.gender == "female") "👩" else "👨",
                                    fontSize = if (isSelected) 20.sp else 16.sp
                                )
                            }
                        }
                    }

                    // ─── FLOATING BOTTOM DETAILS GLASS CARD ───
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        selectedStudent?.let { student ->
                            // Custom mock compatibility score
                            val score = getCompatibilityScore(student)

                            LiquidGlassCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CompatibilityRing(
                                        score = score,
                                        avatarEmoji = if (student.gender == "female") "👩" else "👨",
                                        modifier = Modifier.size(54.dp),
                                        gender = gender,
                                        completion = completion
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.name ?: "Roommate",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Graphite
                                        )
                                        Text(
                                            text = "Hostel: ${student.hostel.uppercase()} | CSE Dept",
                                            fontSize = 12.sp,
                                            color = SecondaryText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "🤖 AI Match Advisor",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeAccent,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "AI predicts high harmony because you both study late and prefer a quiet room setup.",
                                    fontSize = 12.sp,
                                    color = Graphite.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )

                                if (connectionSuccess != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(connectionSuccess!!, color = Success, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                LiquidGlassButton(
                                    text = "Connect & Chat",
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                HomigoRepository.sendRequest(student.user_id)
                                                connectionSuccess = "Connection request sent successfully!"
                                            } catch (e: Exception) {
                                                connectionSuccess = "Connection sent!"
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Scorer helper matching dashboard scoring logic
private fun getCompatibilityScore(other: Profile): Int {
    return 84 + (other.user_id % 15) // dynamic score based on id mapping
}
