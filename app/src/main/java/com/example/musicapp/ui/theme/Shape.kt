package com.example.musicapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * App corner-radius scale, consumed via [MaterialTheme.shapes].
 * Extra tokens that don't map to Material's 5 slots live in [AppCorners].
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/** Fixed shapes for specific components (pills, sheets, full-round avatars). */
object AppCorners {
    val pill = RoundedCornerShape(percent = 50)
    val bottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val card = RoundedCornerShape(20.dp)
    val cover = RoundedCornerShape(14.dp)
}
