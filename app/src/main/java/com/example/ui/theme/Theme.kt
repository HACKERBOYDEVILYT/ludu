package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RsDarkColorScheme = darkColorScheme(
    primary = RsGold,
    onPrimary = Color.Black,
    primaryContainer = RsSurfaceElevated,
    onPrimaryContainer = RsGoldLight,
    secondary = RsAccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = RsSurfaceVariantDark,
    onSecondaryContainer = RsAccentCyan,
    tertiary = RsAccentPurple,
    onTertiary = Color.Black,
    background = RsBackgroundDark,
    onBackground = TextPrimary,
    surface = RsSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = RsSurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

private val RsLightColorScheme = lightColorScheme(
    primary = RsGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E1),
    onPrimaryContainer = RsGoldDark,
    secondary = Color(0xFF0097A7),
    onSecondary = Color.White,
    background = Color(0xFFF7F5FA),
    onBackground = Color(0xFF1B1822),
    surface = Color.White,
    onSurface = Color(0xFF1B1822),
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = Color(0xFF4A4458)
)

@Composable
fun RSLudoTheme(
    darkTheme: Boolean = true, // Default to premium dark theme for game aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RsDarkColorScheme else RsLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
