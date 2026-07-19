package com.example.musicapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

/** Cover/artwork image with a themed placeholder while loading or on error. */
@Composable
fun NetworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholderIcon: ImageVector = Icons.Filled.MusicNote,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { PlaceholderBox(placeholderIcon) },
        error = { PlaceholderBox(placeholderIcon) },
    )
}

/** Circular user avatar with a person placeholder. */
@Composable
fun UserAvatar(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    NetworkImage(
        url = url,
        contentDescription = contentDescription,
        modifier = modifier,
        placeholderIcon = Icons.Filled.Person,
    )
}

@Composable
private fun PlaceholderBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
