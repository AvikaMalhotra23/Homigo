package com.example.homigo.ui.theme

import androidx.compose.ui.graphics.Color

// Semantic Custom Color System
val Primary = Color(0xFF38BDF8)
val PrimaryDark = Color(0xFF0EA5E9)
val Secondary = Color(0xFF34D399) // Mint
val Accent = Color(0xFF2DD4BF)

val Background = Color(0xFFF8FAFC) // Ice White
val Surface = Color(0xFFFFFFFF)
val SurfaceVariant = Color(0xFFF1F5F9)

val Graphite = Color(0xFF1F2937)
val SecondaryText = Color(0xFF64748B)
val Border = Color(0xFFE2E8F0)

val Success = Color(0xFF10B981)
val Warning = Color(0xFFF59E0B)
val Error = Color(0xFFEF4444)

// Keep existing references mapped to the new palette to avoid compilation errors
val White = Color(0xFFFFFFFF)
val OffWhite = Background
val DarkText = Graphite
val LightText = SecondaryText
val BorderColor = Border
val ErrorColor = Error

val MalePrimary = Primary
val MaleSecondary = Secondary
val MaleBackground = Background
val MaleSurface = SurfaceVariant

val FemalePrimary = Primary
val FemaleSecondary = Secondary
val FemaleBackground = Background
val FemaleSurface = SurfaceVariant
