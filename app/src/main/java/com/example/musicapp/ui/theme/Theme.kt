package com.example.musicapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Violet500,
    onPrimary = White,
    primaryContainer = Violet100,
    onPrimaryContainer = Violet700,
    secondary = Magenta500,
    onSecondary = White,
    secondaryContainer = Magenta200,
    onSecondaryContainer = Color(0xFF5C0033),
    tertiary = Aqua500,
    onTertiary = White,
    tertiaryContainer = Aqua200,
    onTertiaryContainer = Color(0xFF003330),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = Color(0xFFE1DDEC),
    error = ErrorLight,
    onError = White,
    scrim = Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Violet300,
    onPrimary = Color(0xFF20124D),
    primaryContainer = Violet600,
    onPrimaryContainer = Violet50,
    secondary = Magenta400,
    onSecondary = Color(0xFF3A0021),
    secondaryContainer = Color(0xFF7A1050),
    onSecondaryContainer = Magenta200,
    tertiary = Aqua400,
    onTertiary = Color(0xFF00201E),
    tertiaryContainer = Aqua500,
    onTertiaryContainer = Aqua200,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF2C2540),
    error = ErrorDark,
    onError = Color(0xFF3A0002),
    scrim = Black,
)

/**
 * Root theme. Wires our custom [colorScheme], [AppTypography], [AppShapes] and
 * provides the [Spacing] scale via [LocalSpacing].
 *
 * @param dynamicColor Material-You wallpaper colors. Defaults to false so the
 *   brand palette always shows; can be enabled from Settings later.
 */
@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
