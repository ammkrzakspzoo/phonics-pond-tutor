package com.phonicspond.tutor.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PondColorScheme = lightColorScheme(
    primary = FrogGreen,
    onPrimary = TextOnPrimary,
    primaryContainer = FrogLight,
    onPrimaryContainer = PondNight,
    secondary = LilyPink,
    onSecondary = TextOnPrimary,
    secondaryContainer = WaterDeep,
    onSecondaryContainer = PondNight,
    tertiary = RewardGold,
    onTertiary = PondNight,
    background = WaterBase,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = WaterDeep,
    onSurfaceVariant = TextSecondary,
    outline = FrogGreen,
    error = LilyPinkDark,
    onError = TextOnPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PondColorScheme,
        typography = PondTypography,
        content = content
    )
}
