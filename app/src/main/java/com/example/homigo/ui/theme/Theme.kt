package com.example.homigo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
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

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Color(0xFF0F172A),       // Slate 900
    surface = Color(0xFF1E293B),          // Slate 800
    surfaceVariant = Color(0xFF334155),   // Slate 700
    onPrimary = Graphite,
    onSecondary = Graphite,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = SecondaryText,
    outline = Color(0xFF475569),
    error = Color(0xFFFCA5A5)
)

// Global custom Shapes
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(20.dp),      // Cards: 20dp corners
    large = RoundedCornerShape(16.dp),       // Buttons: 16dp corners (if shape is read from theme)
    extraLarge = RoundedCornerShape(20.dp)   // Dialogs: 20dp corners
)

@Composable
fun HomigoTheme(
    gender: String = "male", // preserved for backward compatibility
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
