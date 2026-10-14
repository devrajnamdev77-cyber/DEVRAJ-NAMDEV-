package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TradingColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = GoldMuted,
    onPrimaryContainer = Color.White,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = TextSecondary,
    tertiary = BullishGreen,
    onTertiary = Color.White,
    background = BackgroundDark,
    onBackground = TextSecondary,
    surface = CardBackground,
    onSurface = TextSecondary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = BorderDark,
    outlineVariant = BorderLight,
    error = BearishRed,
    onError = Color.White
)

@Composable
fun GoldBabaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TradingColorScheme,
        typography = Typography,
        content = content
    )
}
