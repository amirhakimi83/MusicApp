package com.example.musicapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Central spacing scale. Screens must read padding/margins from here
 * (`MaterialTheme.spacing.medium`) instead of hardcoding `.dp` values.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp,

    // Component-specific tokens
    val screen: Dp = 16.dp,        // default screen horizontal padding
    val itemGap: Dp = 12.dp,       // gap between list items
    val sectionGap: Dp = 24.dp,    // gap between page sections
    val iconSmall: Dp = 18.dp,
    val icon: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val miniPlayerHeight: Dp = 64.dp,
    val bottomBarHeight: Dp = 72.dp,
    val coverSmall: Dp = 56.dp,
    val coverMedium: Dp = 120.dp,
    val coverLarge: Dp = 160.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

/** Convenience accessor: `MaterialTheme.spacing.medium`. */
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
