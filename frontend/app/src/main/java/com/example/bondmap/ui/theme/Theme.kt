package com.example.bondmap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BondMapColors.Navy,
    onPrimary = BondMapColors.TextOnNavy,
    primaryContainer = BondMapColors.NavyDeep,
    onPrimaryContainer = BondMapColors.TextOnNavy,
    secondary = BondMapColors.Accent,
    onSecondary = BondMapColors.TextOnNavy,
    secondaryContainer = BondMapColors.AccentSoft,
    onSecondaryContainer = BondMapColors.NavyDeep,
    error = BondMapColors.Danger,
    onError = BondMapColors.TextOnNavy,
    errorContainer = BondMapColors.DangerSoft,
    onErrorContainer = BondMapColors.Danger,
    background = BondMapColors.Canvas,
    onBackground = BondMapColors.TextPrimary,
    surface = BondMapColors.Surface,
    onSurface = BondMapColors.TextPrimary,
    surfaceVariant = BondMapColors.ChipBg,
    onSurfaceVariant = BondMapColors.TextSecondary,
    outline = BondMapColors.Divider
)

@Composable
fun BondMapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = BondMapTypography,
        shapes = BondMapShapes,
        content = content
    )
}
