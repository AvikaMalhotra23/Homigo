package com.example.homigo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// Keep legacy placeholders (in case referenced elsewhere), but the app now uses
// explicit Homigo color schemes inside HomigoTheme().
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    onPrimary = Color.White,
    onSecondary = Surface,
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    outline = Border,
    error = Error
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Graphite,
    onBackground = Graphite,
    onSurface = Graphite,
    outline = Border,
    error = Error
)


@Composable
fun HomigoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color intentionally disabled to keep the premium brand palette.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Build.VERSION + dynamic color imports kept to avoid changing public surface,
    // but dynamicColor is forced off by default per design constraints.
    val _ = dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Primary,
            secondary = Secondary,
            tertiary = Accent,

            background = Color(0xFF0B1220),
            surface = Color(0xFF111827),
            surfaceVariant = Color(0xFF0F172A),

            onPrimary = Color.White,
            onSecondary = Surface,
            onBackground = Color(0xFFE5E7EB),
            onSurface = Color(0xFFE5E7EB),

            outline = Border,
            error = Error
        )
    } else {
        lightColorScheme(
            primary = Primary,
            secondary = Secondary,
            tertiary = Accent,

            background = Background,
            surface = Surface,
            surfaceVariant = SurfaceVariant,

            onPrimary = Color.White,
            onSecondary = Graphite,
            onBackground = Graphite,
            onSurface = Graphite,

            outline = Border,
            error = Error
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
