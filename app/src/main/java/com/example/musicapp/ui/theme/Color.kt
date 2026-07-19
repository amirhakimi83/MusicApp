package com.example.musicapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw brand color tokens.
 *
 * These are the ONLY place literal color values live. Everything in the UI must
 * consume colors through [MaterialTheme.colorScheme] (or the brand gradients
 * below) — never hardcode a Color in a screen.
 */

// ---- Brand: Violet (primary) ----
val Violet50 = Color(0xFFF1ECFE)
val Violet100 = Color(0xFFD9C9FF)
val Violet200 = Color(0xFFB9A3FF)
val Violet300 = Color(0xFF9A7CFF)
val Violet400 = Color(0xFF8259FA)
val Violet500 = Color(0xFF6C3DF5) // core brand
val Violet600 = Color(0xFF5A2FE0)
val Violet700 = Color(0xFF4820B8)

// ---- Brand: Magenta (secondary) ----
val Magenta200 = Color(0xFFFFA6D2)
val Magenta400 = Color(0xFFF5589C)
val Magenta500 = Color(0xFFEC2E82)

// ---- Brand: Aqua (tertiary / accents) ----
val Aqua200 = Color(0xFF8DEDE8)
val Aqua400 = Color(0xFF20C7BE)
val Aqua500 = Color(0xFF0FB0A7)

// ---- Neutrals: Light ----
val LightBackground = Color(0xFFF7F6FB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFECEAF3)
val LightOnSurface = Color(0xFF1A1626)
val LightOnSurfaceVariant = Color(0xFF544F63)
val LightOutline = Color(0xFFC9C4D6)

// ---- Neutrals: Dark ----
val DarkBackground = Color(0xFF0E0B14)
val DarkSurface = Color(0xFF17131F)
val DarkSurfaceVariant = Color(0xFF241D33)
val DarkOnSurface = Color(0xFFF3F0FA)
val DarkOnSurfaceVariant = Color(0xFFB6AECB)
val DarkOutline = Color(0xFF3A3350)

// ---- Semantic ----
val ErrorLight = Color(0xFFE5484D)
val ErrorDark = Color(0xFFFF6369)
val SuccessColor = Color(0xFF30A46C)
val WarningColor = Color(0xFFF5A524)

// ---- Static ----
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
