package com.example.musicapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Reusable brand gradients. Screens should reference these rather than
 * building ad-hoc brushes, so the visual language stays consistent.
 */
object AppGradients {

    /** Primary brand sweep — used on the carousel and primary CTAs. */
    val brand: Brush = Brush.linearGradient(
        colors = listOf(Violet500, Magenta500),
    )

    /** Cool accent — playlists / discovery cards. */
    val cool: Brush = Brush.linearGradient(
        colors = listOf(Violet400, Aqua400),
    )

    /** Gold sweep for the Premium badge / upsell. */
    val premium: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFD86B), Color(0xFFF5A524)),
    )

    /**
     * Vertical scrim used behind cover art / above the mini-player so text
     * stays legible over artwork.
     */
    fun coverScrim(base: Color): Brush = Brush.verticalGradient(
        colors = listOf(Color.Transparent, base.copy(alpha = 0.85f)),
    )

    /**
     * Player background gradient built from a Palette-extracted dominant color.
     * Falls back gracefully when [dominant] is null (uses the brand color).
     */
    fun player(dominant: Color?, surface: Color): Brush = Brush.verticalGradient(
        colors = listOf(
            (dominant ?: Violet500).copy(alpha = 0.9f),
            (dominant ?: Violet500).copy(alpha = 0.35f),
            surface,
        ),
    )
}
