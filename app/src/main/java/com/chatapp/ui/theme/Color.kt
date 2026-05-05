package com.chatapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFF2563EB)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFDBEAFE)
val LightOnPrimaryContainer = Color(0xFF1E3A5F)

val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOnSurface = Color(0xFF0F172A)
val LightOnSurfaceVariant = Color(0xFF64748B)

val LightAccent = Color(0xFF06D6A0)
val LightAccentContainer = Color(0xFFD1FAE5)
val LightDanger = Color(0xFFEF4444)
val LightDangerContainer = Color(0xFFFEE2E2)

val DarkPrimary = Color(0xFF3B82F6)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkPrimaryContainer = Color(0xFF1E3A5F)
val DarkOnPrimaryContainer = Color(0xFFDBEAFE)

val DarkBackground = Color(0xFF0B1120)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkOnSurface = Color(0xFFF1F5F9)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

val DarkAccent = Color(0xFF34D399)
val DarkAccentContainer = Color(0xFF064E3B)
val DarkDanger = Color(0xFFF87171)
val DarkDangerContainer = Color(0xFF450A0A)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightDanger,
    errorContainer = LightDangerContainer,
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9)
)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkDanger,
    errorContainer = DarkDangerContainer,
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
)

val LocalThemeColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightAccent,
    tertiary = Color(0xFF8B5CF6),
    primaryContainer = LightPrimaryContainer,
    secondaryContainer = LightAccentContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightDanger,
    errorContainer = LightDangerContainer,
    outline = Color(0xFFE2E8F0),
)

val LocalDarkThemeColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkAccent,
    tertiary = Color(0xFFA78BFA),
    primaryContainer = DarkPrimaryContainer,
    secondaryContainer = DarkAccentContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkDanger,
    errorContainer = DarkDangerContainer,
    outline = Color(0xFF334155),
)
