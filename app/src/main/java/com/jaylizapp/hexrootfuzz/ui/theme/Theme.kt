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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HexRed,
    secondary = HexDarkGrey,
    tertiary = HexBrightRed,
    background = HexBlack,
    surface = HexBlack,
    onPrimary = HexWhite,
    onSecondary = HexWhite,
    onTertiary = HexWhite,
    onBackground = HexWhite,
    onSurface = HexWhite,
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
