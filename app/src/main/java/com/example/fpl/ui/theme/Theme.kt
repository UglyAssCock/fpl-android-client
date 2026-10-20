package com.example.fpl.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FplBgNavy = Color(0xFF070A21)
val FplCardNavy = Color(0xFF10163A)
val FplCardNavyLight = Color(0xFF161E4A)
val FplCardBorder = Color(0xFF252F66)
val FplElectricPurple = Color(0xFF6C38FF)
val FplBrightPurple = Color(0xFF8B5CF6)
val FplAccentGreen = Color(0xFF00E676)
val FplOutCrimson = Color(0xFFE11D48)
val FplTextSecondary = Color(0xFF94A3B8)

private val FplExactDarkColorScheme = darkColorScheme(
    primary = FplElectricPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF281B68),
    onPrimaryContainer = Color.White,
    secondary = FplAccentGreen,
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF161E4A),
    onSecondaryContainer = Color.White,
    background = FplBgNavy,
    surface = FplCardNavy,
    surfaceVariant = FplCardNavyLight,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = FplTextSecondary,
    error = FplOutCrimson,
    onError = Color.White
)

@Composable
fun FplTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FplExactDarkColorScheme,
        content = content
    )
}
