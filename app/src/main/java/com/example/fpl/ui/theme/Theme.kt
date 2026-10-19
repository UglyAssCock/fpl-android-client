package com.example.fpl.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FplDarkColors = darkColorScheme(
    primary = Color(0xFF00FF87),
    onPrimary = Color(0xFF37003C),
    primaryContainer = Color(0xFF37003C),
    onPrimaryContainer = Color(0xFF00FF87),
    secondary = Color(0xFF05F0FF),
    onSecondary = Color(0xFF002B36),
    background = Color(0xFF120916),
    surface = Color(0xFF1E1224),
    onBackground = Color(0xFFF5F2F7),
    onSurface = Color(0xFFF5F2F7)
)

private val FplLightColors = lightColorScheme(
    primary = Color(0xFF37003C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF37003C),
    secondary = Color(0xFF00885A),
    onSecondary = Color.White,
    background = Color(0xFFF8F7FA),
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun FplTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FplDarkColors else FplLightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
