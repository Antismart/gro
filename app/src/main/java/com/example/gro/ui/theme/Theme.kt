package com.example.gro.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GroColorScheme = lightColorScheme(
    primary = GroGreen,
    onPrimary = GroWhite,
    primaryContainer = GroSage,
    onPrimaryContainer = GroBark,
    secondary = GroSage,
    onSecondary = GroBark,
    secondaryContainer = GroSand,
    onSecondaryContainer = GroBark,
    tertiary = GroSunlight,
    onTertiary = GroBark,
    tertiaryContainer = GroSunlight,
    onTertiaryContainer = GroBark,
    background = GroCream,
    onBackground = GroBark,
    surface = GroSurface,
    onSurface = GroBark,
    surfaceVariant = GroSand,
    onSurfaceVariant = GroEarth,
    error = GroError,
    onError = GroWhite,
    outline = GroDivider,
    outlineVariant = GroDivider,
)

@Composable
fun GroTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = GroCream.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = GroColorScheme,
        typography = GroTypography,
        shapes = GroShapes,
        content = content,
    )
}
