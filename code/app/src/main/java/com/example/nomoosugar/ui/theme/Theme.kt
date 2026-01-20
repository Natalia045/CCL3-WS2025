package com.example.nomoosugar.ui.theme

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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.nomoosugar.ui.theme.NavBarDarkGray
import com.example.nomoosugar.ui.theme.NavBarGray
import com.example.nomoosugar.ui.theme.CardBackgroundBlue

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6), // A vibrant light blue for selected items and accents
    onPrimary = Color.White, // Changed for better visibility on dark primaryContainer
    primaryContainer = Color(0xFF004C8C), // A darker blue for backgrounds that contain primary elements
    onPrimaryContainer = Color.White, // White for text on primaryContainer
    surface = NavBarDarkGray, // Dark gray for top bar and nav bar backgrounds
    onSurface = Color(0xFFE0E0E0), // Light gray for text on surface
    background = Color(0xFF121212), // Very dark background
    onBackground = Color(0xFFE0E0E0), // Light gray for text on background
    surfaceVariant = Color(0xFF37474F), // Dark blue-gray for cards and progress track
    onSurfaceVariant = Color(0xFFE0E0E0), // Light gray for text on surfaceVariant
    secondary = PurpleGrey40, // Keeping these as fallbacks if not explicitly used
    tertiary = Pink40
)

private val LightColorScheme = lightColorScheme(
    primary = HomeTitleBlue,
    onPrimary = Color.White,
    primaryContainer = HomeTitleBlue,
    onPrimaryContainer = Color.White,
    surface = NavBarGray,
    onSurface = HomeTitleBlue,
    background = Color.White,
    onBackground = AppBlack,
    surfaceVariant = CardBackgroundBlue,
    onSurfaceVariant = AppBlack,
    secondary = PurpleGrey40,
    tertiary = Pink40
)


@Composable
fun NoMooSugarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // To make the clock on the top dark
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val statusBarColor = if (darkTheme) colorScheme.surface else CardBackgroundBlue
            window.statusBarColor = statusBarColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}