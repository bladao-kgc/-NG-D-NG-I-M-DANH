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
      primary = Color(0xFF9ECAFF),
      onPrimary = Color(0xFF003258),
      primaryContainer = Color(0xFF00497D),
      onPrimaryContainer = Color(0xFFD1E4FF),
      secondary = Color(0xFFBBC7DB),
      onSecondary = Color(0xFF253140),
      background = Color(0xFF1B1B1F),
      onBackground = Color(0xFFE3E2E6),
      surface = Color(0xFF1B1B1F),
      onSurface = Color(0xFFE3E2E6),
      surfaceVariant = Color(0xFF43474E),
      onSurfaceVariant = Color(0xFFC3C6CF)
  )

private val LightColorScheme =
  lightColorScheme(
      primary = VibrantPrimary,
      onPrimary = VibrantOnPrimary,
      primaryContainer = VibrantPrimaryContainer,
      onPrimaryContainer = VibrantOnPrimaryContainer,
      secondary = VibrantSecondary,
      onSecondary = Color.White,
      secondaryContainer = VibrantSecondaryContainer,
      onSecondaryContainer = VibrantOnSecondaryContainer,
      background = VibrantBackground,
      onBackground = VibrantOnBackground,
      surface = VibrantSurface,
      onSurface = VibrantOnSurface,
      surfaceVariant = VibrantSurfaceVariant,
      onSurfaceVariant = VibrantOnSurfaceVariant,
      outline = VibrantOutline,
      error = VibrantError
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

