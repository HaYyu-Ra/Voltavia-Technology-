package com.example.ui.theme

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
    primary = VoltaviaPrimaryDark,
    onPrimary = VoltaviaOnPrimaryDark,
    primaryContainer = VoltaviaPrimaryContainerDark,
    onPrimaryContainer = VoltaviaOnPrimaryContainerDark,
    secondary = VoltaviaSecondaryDark,
    onSecondary = VoltaviaOnSecondaryDark,
    secondaryContainer = VoltaviaSecondaryContainerDark,
    onSecondaryContainer = VoltaviaOnSecondaryContainerDark,
    tertiary = VoltaviaTertiary,
    background = VoltaviaBackgroundDark,
    onBackground = VoltaviaOnBackgroundDark,
    surface = VoltaviaSurfaceDark,
    onSurface = VoltaviaOnSurfaceDark,
    surfaceVariant = VoltaviaSurfaceVariantDark,
    onSurfaceVariant = VoltaviaOnSurfaceVariantDark,
    outline = VoltaviaOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = VoltaviaPrimary,
    onPrimary = VoltaviaOnPrimary,
    primaryContainer = VoltaviaPrimaryContainer,
    onPrimaryContainer = VoltaviaOnPrimaryContainer,
    secondary = VoltaviaSecondary,
    onSecondary = VoltaviaOnSecondary,
    secondaryContainer = VoltaviaSecondaryContainer,
    onSecondaryContainer = VoltaviaOnSecondaryContainer,
    tertiary = VoltaviaTertiary,
    background = VoltaviaBackgroundLight,
    onBackground = VoltaviaOnBackgroundLight,
    surface = VoltaviaSurfaceLight,
    onSurface = VoltaviaOnSurfaceLight,
    surfaceVariant = VoltaviaSurfaceVariantLight,
    onSurfaceVariant = VoltaviaOnSurfaceVariantLight,
    outline = VoltaviaOutlineLight
)

@Composable
fun VoltaviaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep branded colors by default for consistent corporate visual identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VoltaviaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
