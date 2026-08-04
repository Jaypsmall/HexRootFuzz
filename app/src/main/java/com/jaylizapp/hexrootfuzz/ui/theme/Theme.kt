package com.jaylizapp.hexrootfuzz.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HexAccent,
    secondary = HexPanel,
    tertiary = HexAccentLow,
    background = HexBg,
    surface = HexPanel,
    onPrimary = Color.White,
    onSecondary = HexText,
    onTertiary = Color.White,
    onBackground = HexText,
    onSurface = HexText,
)

@Composable
fun HexRootFuzzTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
