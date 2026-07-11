package com.example.homigo.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── GLASS DENSITY ────────────────────────────────────────────────────────────
enum class GlassDensity {
    HIGH,   // Maximum transparency, strong frosted blur (e.g. Hero, Dock, Match Cards)
    MEDIUM, // Balanced opacity, clean frosted overlay (e.g. Dialogs, Filters)
    LOW     // High opacity, subtle glass highlights (e.g. Chips, Lists, Buttons)
}

// ─── PREMIUM GRADIENT & LAYERED AURORA BACKGROUND ──────────────────────────────
@Composable
fun FloatingBackground(
    gender: String,
    completion: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isCompleted = completion >= 100

    // Dynamic color values depending on gender and completion
    val bgStart = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFFFAFBFD) // Neutral light
        else if (gender.lowercase() == "female") Color(0xFFFFF0F5) // Soft Lavender Blush
        else Color(0xFFF4F9FF) // Ice Blue
    }
    val bgEnd = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFFF1F4F9) // Neutral slate
        else if (gender.lowercase() == "female") Color(0xFFFFF0F3) // Soft Rose Peach
        else Color(0xFFE8F2FE)
    }

    val primaryGlow = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFF38BDF8).copy(alpha = 0.16f) // Sky Blue accent
        else if (gender.lowercase() == "female") Color(0xFFFDA4AF).copy(alpha = 0.2f) // Peach/Rose
        else Color(0xFF3B82F6).copy(alpha = 0.18f) // Royal Blue
    }

    val secondaryGlow = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFF818CF8).copy(alpha = 0.12f)
        else if (gender.lowercase() == "female") Color(0xFFE9D5FF).copy(alpha = 0.16f) // Lavender
        else Color(0xFF06B6D4).copy(alpha = 0.14f) // Cyan Glow
    }

    // Aurora movement animation state
    val infiniteTransition = rememberInfiniteTransition(label = "auroraMovement")
    val auroraOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    val auroraOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -120f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(bgStart, bgEnd, Color.White)
                )
            )
    ) {
        // Draw moving aurora layers and floating particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Layer 1: Left Aurora Flow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryGlow, Color.Transparent),
                    center = Offset(w * 0.2f + auroraOffset1, h * 0.3f)
                ),
                radius = w * 0.65f,
                center = Offset(w * 0.2f + auroraOffset1, h * 0.3f)
            )

            // Layer 2: Right Aurora Flow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondaryGlow, Color.Transparent),
                    center = Offset(w * 0.8f + auroraOffset2, h * 0.7f)
                ),
                radius = w * 0.7f,
                center = Offset(w * 0.8f + auroraOffset2, h * 0.7f)
            )

            // Subtle floating particles specs (Nothing OS inspired)
            val seedRandom = java.util.Random(42)
            for (i in 0..12) {
                val px = seedRandom.nextFloat() * w
                val py = (seedRandom.nextFloat() * h + (auroraOffset1 * 0.5f)) % h
                val sizeVal = seedRandom.nextFloat() * 4f + 2f
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = sizeVal,
                    center = Offset(px, py)
                )
            }
        }
        content()
    }
}

// ─── LIQUID GLASS CARD CONTAINER ─────────────────────────────────────────────
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    density: GlassDensity = GlassDensity.HIGH,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 30.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "liquidCardScale"
    )

    val containerAlpha = when (density) {
        GlassDensity.HIGH -> 0.42f
        GlassDensity.MEDIUM -> 0.65f
        GlassDensity.LOW -> 0.88f
    }

    val shadowElevation = when (density) {
        GlassDensity.HIGH -> 8.dp
        GlassDensity.MEDIUM -> 4.dp
        GlassDensity.LOW -> 2.dp
    }

    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = modifier
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .shadow(
            elevation = if (isPressed) 2.dp else shadowElevation,
            shape = RoundedCornerShape(cornerRadius),
            clip = false,
            ambientColor = Color.Black.copy(alpha = 0.02f),
            spotColor = Color.Black.copy(alpha = 0.06f)
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.White.copy(alpha = containerAlpha))
        // Specular double-highlight border (liquid edge highlight)
        .drawBehind {
            // Inner highlight drawn at the top edge
            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(1f, 1f),
                size = Size(size.width - 2f, 4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
            )
        }
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = if (density == GlassDensity.HIGH) 0.55f else 0.35f),
            shape = RoundedCornerShape(cornerRadius)
        )

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(bounded = true, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            onClick = {
                isPressed = true
                onClick()
                isPressed = false
            }
        )
    } else {
        baseModifier
    }

    Column(
        modifier = finalModifier.padding(22.dp),
        content = content
    )
}

// ─── LIQUID GLASS CAPSULE BUTTON ──────────────────────────────────────────────
@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    density: GlassDensity = GlassDensity.LOW,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val finalContainerColor = containerColor ?: when (density) {
        GlassDensity.LOW -> MaterialTheme.colorScheme.primary
        else -> Color.White.copy(alpha = 0.55f)
    }
    
    val finalContentColor = contentColor ?: when (density) {
        GlassDensity.LOW -> Color.White
        else -> MaterialTheme.colorScheme.primary
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(23.dp), // Complete capsule
        colors = ButtonDefaults.buttonColors(
            containerColor = finalContainerColor,
            contentColor = finalContentColor,
            disabledContainerColor = Color.Black.copy(alpha = 0.05f),
            disabledContentColor = Color.Black.copy(alpha = 0.25f)
        ),
        border = if (density != GlassDensity.LOW) BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)) else null,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 0.2.sp
        )
    }
}

// ─── AVATAR WITH LIQUID COMPATIBILITY RING ────────────────────────────────────
@Composable
fun CompatibilityRing(
    score: Int,
    avatarEmoji: String,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    gender: String = "male",
    completion: Int = 0
) {
    val isCompleted = completion >= 100
    val primaryRing = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFF0284C7)
        else if (gender.lowercase() == "female") Color(0xFFEC4899)
        else Color(0xFF2563EB)
    }
    val secondaryRing = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFF38BDF8)
        else if (gender.lowercase() == "female") Color(0xFFFDA4AF)
        else Color(0xFF06B6D4)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Track
            drawCircle(
                color = primaryRing.copy(alpha = 0.07f),
                style = Stroke(width = strokeWidth.toPx())
            )
            // Arc
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(primaryRing, secondaryRing)
                ),
                startAngle = -90f,
                sweepAngle = (score / 100f) * 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Inner Glass Circle containing Avatar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize(0.78f)
                .shadow(2.dp, CircleShape)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        ) {
            Text(text = avatarEmoji, fontSize = 20.sp)
        }
    }
}

// ─── LIQUID GLASS CHIP FOR TAGS ───────────────────────────────────────────────
@Composable
fun LiquidGlassChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Graphite.copy(alpha = 0.8f)
        )
    }
}

// ─── ANDROID GLASS NAVIGATION BAR DOCK ────────────────────────────────────────
@Composable
fun FloatingGlassBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<BottomTab>,
    gender: String,
    completion: Int
) {
    val isCompleted = completion >= 100
    val activeAccent = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFF0284C7)
        else if (gender.lowercase() == "female") Color(0xFFEC4899)
        else Color(0xFF2563EB)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(32.dp), clip = false)
                .background(Color.White.copy(alpha = 0.65f), RoundedCornerShape(32.dp))
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = selectedTab == index
                val scale by animateFloatAsState(if (selected) 1.22f else 1.0f, label = "dockTabScale")

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (selected) activeAccent.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true, color = activeAccent.copy(alpha = 0.15f))
                        ) {
                            onTabSelected(index)
                        }
                ) {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                        tint = if (selected) activeAccent else SecondaryText,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                }
            }
        }
    }
}

// ─── TAB DATA REPRESENTATION ──────────────────────────────────────────────────
data class BottomTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
