package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = PlaybeatBlue,
    onPrimary = NavyBlack,
    primaryContainer = NavySurfaceVariant,
    onPrimaryContainer = PlaybeatBlue,
    secondary = PlaybeatOrange,
    onSecondary = Color.White,
    secondaryContainer = NavySurfaceVariant,
    onSecondaryContainer = PlaybeatOrangeGlow,
    tertiary = PlaybeatSilver,
    onTertiary = NavyBlack,
    background = NavyBlack,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyCardBorder,
    outlineVariant = GlassBorder,
    error = ErrorRed,
    onError = Color.White,
  )

private val LightColorScheme =
  darkColorScheme(
    primary = PlaybeatBlue,
    onPrimary = NavyBlack,
    secondary = PlaybeatOrange,
    onSecondary = Color.White,
    background = NavyBlack,
    surface = NavySurface,
    onSurface = TextPrimary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to brand's dark aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
