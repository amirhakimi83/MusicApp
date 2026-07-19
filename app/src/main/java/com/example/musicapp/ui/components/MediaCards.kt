package com.example.musicapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing

/** Vertical song card used in horizontal rows (Home, playlist detail…). */
@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp,
) {
    Column(
        modifier = modifier
            .width(width)
            .clip(AppCorners.card)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        NetworkImage(
            url = song.coverImageUrl,
            contentDescription = song.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(AppCorners.cover),
        )
        Text(
            text = song.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Vertical playlist card. */
@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
) {
    Column(
        modifier = modifier
            .width(width)
            .clip(AppCorners.card)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        NetworkImage(
            url = playlist.coverImageUrl,
            contentDescription = playlist.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(AppCorners.cover),
        )
        Text(
            text = playlist.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = pluralStringResource(R.plurals.song_count, playlist.songCount, playlist.songCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

/** Circular artist card. */
@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
) {
    Column(
        modifier = modifier
            .width(size)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        NetworkImage(
            url = artist.imageUrl,
            contentDescription = artist.name,
            placeholderIcon = Icons.Filled.Person,
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
        )
        Text(
            text = artist.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
