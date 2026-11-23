package com.raisoftlabs.computerkingdom.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF0B1026)
val InkSoft = Color(0xFF151C3B)
val Panel = Color(0xFF1B254E)
val PanelLight = Color(0xFF25315F)
val Indigo = Color(0xFF6366F1)
val Cyan = Color(0xFF22D3EE)
val Gold = Color(0xFFFBBF24)
val Coral = Color(0xFFFF7A7A)
val Mint = Color(0xFF34D399)
val Lavender = Color(0xFFA78BFA)
val Cream = Color(0xFFF8FAFF)
val Muted = Color(0xFFA9B4D8)

val LocalTextScale = compositionLocalOf { 1f }

@Composable
fun scaledSp(value: Float): TextUnit = (value * LocalTextScale.current).sp

private val kingdomTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 34.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp),
)

private val kingdomColors = darkColorScheme(
    primary = Indigo,
    onPrimary = Cream,
    secondary = Cyan,
    onSecondary = Ink,
    tertiary = Gold,
    onTertiary = Ink,
    background = Ink,
    onBackground = Cream,
    surface = Panel,
    onSurface = Cream,
    surfaceVariant = PanelLight,
    onSurfaceVariant = Muted,
    error = Coral,
    onError = Ink,
)

@Composable
fun KingdomTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = kingdomColors, typography = kingdomTypography, content = content)
}
