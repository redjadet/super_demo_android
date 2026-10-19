package com.redjadet.superdemo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Mist,
    onPrimaryContainer = ForestDark,
    secondary = ForestDark,
    onSecondary = Color.White,
    background = Sand,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    onSurfaceVariant = InkMuted,
    error = Alert,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DDBA5),
    onPrimary = Color(0xFF00391A),
    primaryContainer = ForestDark,
    onPrimaryContainer = Mist,
    secondary = Color(0xFFA8CDB6),
    onSecondary = Color(0xFF0B1F14),
    background = Color(0xFF0B1F14),
    onBackground = Color(0xFFE4EFE8),
    surface = Color(0xFF13261B),
    onSurface = Color(0xFFE4EFE8),
    onSurfaceVariant = Color(0xFFB4C4BA),
    error = Color(0xFFFFB4AB),
)

@Composable
fun SuperDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SuperDemoTypography,
        content = content,
    )
}
