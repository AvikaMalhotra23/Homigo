package com.example.homigo.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Neutral Light Color Scheme (used before 100% completion)
private val NeutralLightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onPrimary = White,
    onSecondary = Graphite,
    onBackground = Graphite,
    onSurface = Graphite,
    outline = Border,
    error = Error
)

// Male Light Color Scheme (Royal Blue, Sky Blue, Navy accents)
private val MaleLightColorScheme = lightColorScheme(
    primary = MalePrimary,
    secondary = MaleSecondary,
    background = MaleBackground,
    surface = MaleSurface,
    surfaceVariant = Color(0xFFE0F2FE),
    onPrimary = Color.White,
    onSecondary = Color(0xFF0F172A),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    outline = Color(0xFFBAE6FD),
    error = Error
)

// Female Light Color Scheme (Baby Pink, Lavender, Rose gradients)
private val FemaleLightColorScheme = lightColorScheme(
    primary = FemalePrimary,
    secondary = FemaleSecondary,
    background = FemaleBackground,
    surface = FemaleSurface,
    surfaceVariant = Color(0xFFFFF1F2),
    onPrimary = Color.White,
    onSecondary = Color(0xFF0F172A),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    outline = Color(0xFFFBCFE8),
    error = Error
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Color(0xFF0F172A),       // Slate 900
    surface = Color(0xFF1E293B),          // Slate 800
    surfaceVariant = Color(0xFF334155),   // Slate 700
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = SecondaryText,
    outline = Color(0xFF475569),
    error = Color(0xFFFCA5A5)
)

// Global custom Shapes with premium 18–24dp corners
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(24.dp),      // Cards: 24dp corners
    large = RoundedCornerShape(18.dp),       // Buttons: 18dp corners
    extraLarge = RoundedCornerShape(24.dp)   // Dialogs: 24dp corners
)

@Composable
fun HomigoTheme(
    gender: String = "male",
    completion: Int = 0,
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isCompleted = completion >= 100
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        isCompleted && gender.lowercase() == "female" -> FemaleLightColorScheme
        isCompleted && gender.lowercase() == "male" -> MaleLightColorScheme
        else -> NeutralLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}

// Reusable Glassmorphic background with vertical gradients and floating blurred circles
@Composable
fun GlassmorphicBackground(
    gender: String,
    completion: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isCompleted = completion >= 100
    val startColor = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFFF8FAFC) // Neutral slate
        else if (gender.lowercase() == "female") Color(0xFFFFF5F7) // Rose-tinted light
        else Color(0xFFF0F9FF) // Sky Blue
    }
    
    val endColor = remember(gender, isCompleted) {
        if (!isCompleted) Color(0xFFF1F5F9)
        else if (gender.lowercase() == "female") Color(0xFFFFECEF)
        else Color(0xFFE0F2FE)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(startColor, endColor, Color.White)
                )
            )
    ) {
        // Floating blurred circular glow
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.75f)) {
            val width = size.width
            val height = size.height

            // Circle 1
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (!isCompleted) Color(0xFF38BDF8).copy(alpha = 0.2f)
                        else if (gender.lowercase() == "female") Color(0xFFFDA4AF).copy(alpha = 0.22f)
                        else Color(0xFF38BDF8).copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                radius = width * 0.45f,
                center = androidx.compose.ui.geometry.Offset(width * 0.15f, height * 0.25f)
            )

            // Circle 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (!isCompleted) Color(0xFF818CF8).copy(alpha = 0.15f)
                        else if (gender.lowercase() == "female") Color(0xFFF472B6).copy(alpha = 0.18f)
                        else Color(0xFF0EA5E9).copy(alpha = 0.18f),
                        Color.Transparent
                    )
                ),
                radius = width * 0.55f,
                center = androidx.compose.ui.geometry.Offset(width * 0.85f, height * 0.65f)
            )
        }

        content()
    }
}

// Reusable Glassmorphic Card Container
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderStrokeColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Card(
        modifier = cardModifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.72f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderStrokeColor ?: Color.White.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}
