package com.example.homigo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MaleColorScheme = lightColorScheme(
    primary = MalePrimary,
    secondary = MaleSecondary,
    background = MaleBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = BorderColor,
    surfaceVariant = MaleSurface
)

private val FemaleColorScheme = lightColorScheme(
    primary = FemalePrimary,
    secondary = FemaleSecondary,
    background = FemaleBackground,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = BorderColor,
    surfaceVariant = FemaleSurface
)

@Composable
fun HomigoTheme(
    gender: String = "male", // "male" or "female"
    content: @Composable () -> Unit
) {
    val colorScheme = if (gender.lowercase() == "female") {
        FemaleColorScheme
    } else {
        MaleColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
