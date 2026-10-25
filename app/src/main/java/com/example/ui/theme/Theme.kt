package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ApexDarkColorScheme = darkColorScheme(
    primary = AutoRacingRed,
    onPrimary = AutoPureWhite,
    primaryContainer = AutoDarkRed,
    onPrimaryContainer = AutoPureWhite,
    secondary = AutoCrimsonGlow,
    onSecondary = AutoPureWhite,
    secondaryContainer = AutoCardElevatedDark,
    onSecondaryContainer = AutoOffWhite,
    tertiary = AutoTachRedline,
    onTertiary = AutoPureWhite,
    background = AutoPureBlack,
    onBackground = AutoPureWhite,
    surface = AutoDarkCarbon,
    onSurface = AutoPureWhite,
    surfaceVariant = AutoCardSurfaceDark,
    onSurfaceVariant = AutoMutedWhite,
    outline = AutoBorderDark,
    outlineVariant = Color(0xFF1C1F28)
)

val ApexLightColorScheme = lightColorScheme(
    primary = AutoDayRed,
    onPrimary = AutoPureWhite,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF5F000B),
    secondary = Color(0xFF222631),
    onSecondary = AutoPureWhite,
    secondaryContainer = AutoDaySurfaceVariant,
    onSecondaryContainer = AutoDayText,
    tertiary = AutoRacingRed,
    onTertiary = AutoPureWhite,
    background = AutoDayBackground,
    onBackground = AutoDayText,
    surface = AutoDaySurface,
    onSurface = AutoDayText,
    surfaceVariant = AutoDaySurfaceVariant,
    onSurfaceVariant = Color(0xFF4A4E5C),
    outline = AutoDayBorder,
    outlineVariant = Color(0xFFCED3DE)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ApexDarkColorScheme else ApexLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
