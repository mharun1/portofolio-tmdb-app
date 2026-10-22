package com.harun.tmdbapp.ui.theme

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
    primary = DarkThemePrimary,
    onPrimary = DarkNavyPrimary,
    primaryContainer = DarkNavyLight,
    onPrimaryContainer = DarkThemePrimary,
    secondary = DarkGenreChipText,
    onSecondary = DarkNavyPrimary,
    secondaryContainer = DarkGenreChipBackground,
    onSecondaryContainer = DarkGenreChipText,
    background = DarkThemeBackground,
    onBackground = DarkThemeOnSurface,
    surface = DarkThemeSurface,
    onSurface = DarkThemeOnSurface,
    surfaceVariant = DarkThemeSurfaceVariant,
    onSurfaceVariant = DarkThemeOnSurfaceVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = GenreChipBackground,
    onPrimaryContainer = DarkNavyPrimary,
    secondary = GenreChipText,
    onSecondary = Color.White,
    secondaryContainer = GenreChipBackground,
    onSecondaryContainer = GenreChipText,
    background = LightSurfaceBackground,
    onBackground = DarkNavyPrimary,
    surface = LightSurfaceBackground,
    onSurface = DarkNavyPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryGray,
)

@Composable
fun PortofolioTMDBAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Turn off dynamic color by default so the curated FilmKu inspiration palette takes effect
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
