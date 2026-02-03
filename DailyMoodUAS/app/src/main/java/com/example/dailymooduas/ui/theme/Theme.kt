package com.example.dailymooduas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9FB0),
    secondary = Color(0xFFFFC1CC),
    tertiary = Color(0xFFE6D5F5),
    background = Color(0xFFFFFAFB),
    surface = Color(0xFFFFF0F3),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2D2D2D),
    onSurface = Color(0xFF2D2D2D),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9FB0),
    secondary = Color(0xFFFFC1CC),
    tertiary = Color(0xFFE6D5F5)
)

@Composable
fun DailyMoodUASTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}