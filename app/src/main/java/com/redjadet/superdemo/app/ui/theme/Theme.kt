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
    secondaryContainer = Mist,
    onSecondaryContainer = ForestDark,
    tertiary = Color(0xFF705900),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE8A3),
    onTertiaryContainer = Color(0xFF251A00),
    onSecondary = Color.White,
    background = Sand,
    onBackground = Ink,
    surface = Color.White,
    surfaceVariant = Color(0xFFE0EAE3),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F5F1),
    surfaceContainer = Color(0xFFE8F0EA),
    surfaceContainerHigh = Color(0xFFE0EAE3),
    surfaceContainerHighest = Color(0xFFD9E4DC),
    surfaceTint = Forest,
    outline = Color(0xFF6F7D73),
    outlineVariant = Color(0xFFC1CEC4),
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
    secondaryContainer = Color(0xFF284E38),
    onSecondaryContainer = Mist,
    tertiary = Color(0xFFE8C45A),
    onTertiary = Color(0xFF3B2E00),
    tertiaryContainer = Color(0xFF554400),
    onTertiaryContainer = Color(0xFFFFE8A3),
    background = Color(0xFF0B1F14),
    onBackground = Color(0xFFE4EFE8),
    surface = Color(0xFF13261B),
    surfaceVariant = Color(0xFF34483B),
    surfaceContainerLowest = Color(0xFF08180F),
    surfaceContainerLow = Color(0xFF192D20),
    surfaceContainer = Color(0xFF1D3325),
    surfaceContainerHigh = Color(0xFF263D2E),
    surfaceContainerHighest = Color(0xFF304737),
    surfaceTint = Color(0xFF7DDBA5),
    outline = Color(0xFF8C9E91),
    outlineVariant = Color(0xFF3E5345),
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
