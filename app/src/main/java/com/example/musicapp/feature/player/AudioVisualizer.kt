package com.example.musicapp.feature.player

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sin

/**
 * Decorative equalizer drawn with Compose [Canvas] (no Lottie/GIF, per spec).
 * Bars animate while [isPlaying] is true and settle flat when paused.
 */
@Composable
fun AudioVisualizer(
    isPlaying: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    barCount: Int = 36,
) {
    val transition = rememberInfiniteTransition(label = "visualizer")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phase",
    )

    Canvas(modifier = modifier) {
        val slot = size.width / barCount
        val barWidth = slot * 0.5f
        val corner = CornerRadius(barWidth / 2f, barWidth / 2f)
        for (i in 0 until barCount) {
            val fraction = if (isPlaying) {
                0.2f + 0.8f * abs(sin(phase + i * 0.5f))
            } else {
                0.12f
            }
            val barHeight = size.height * fraction
            val x = i * slot + (slot - barWidth) / 2f
            val y = (size.height - barHeight) / 2f
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = corner,
            )
        }
    }
}
