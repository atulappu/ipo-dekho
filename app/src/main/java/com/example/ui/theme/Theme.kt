package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrangeDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4E2200),
    onPrimaryContainer = Color(0xFFFFDBC8),
    secondary = SecondaryBlueDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00325B),
    onSecondaryContainer = Color(0xFFD1E4FF),
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = OnSurfaceSubtleDark,
    outline = BorderDark,
    error = MarketRedDark,
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = Color(0xFF8B2500),
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = SecondaryBlueLight,
    onSecondaryContainer = Color(0xFF002244),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = OnSurfaceSubtleLight,
    outline = BorderLight,
    error = MarketRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
