package com.example.musicapp.feature.player

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Extracts a dominant color from a cover image (Palette API) and animates to it.
 * Used to tint the Now Playing background. Falls back to [fallback] on failure.
 */
@Composable
fun rememberDominantColor(url: String?, fallback: Color): Color {
    val context = LocalContext.current
    var target by remember(url) { mutableStateOf(fallback) }

    LaunchedEffect(url) {
        target = url?.let { extractDominantColor(context, it) } ?: fallback
    }

    val animated by animateColorAsState(targetValue = target, label = "dominantColor")
    return animated
}

private suspend fun extractDominantColor(context: Context, url: String): Color? {
    val bitmap = loadBitmap(context, url) ?: return null
    val palette = withContext(Dispatchers.Default) { Palette.from(bitmap).generate() }
    val rgb = palette.vibrantSwatch?.rgb
        ?: palette.mutedSwatch?.rgb
        ?: palette.dominantSwatch?.rgb
        ?: return null
    return Color(rgb)
}

private suspend fun loadBitmap(context: Context, url: String): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false) // Palette needs to read pixels
        .size(200)
        .build()
    val result = context.imageLoader.execute(request)
    return (result as? SuccessResult)?.drawable?.toBitmap()
}
