package com.example.musicapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing

/**
 * Generic "list of songs" page with an attractive header and Play-all / Shuffle
 * actions. Reused by playlist detail, liked songs, recently played and artist
 * detail. When [onRemove] is provided, rows can be swiped away.
 */
@Composable
fun TrackCollectionScreen(
    title: String,
    songs: List<Song>,
    onBack: () -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onSongClick: (Song) -> Unit,
    @StringRes emptyMessageRes: Int,
    modifier: Modifier = Modifier,
    coverUrl: String? = null,
    onRemove: ((Song) -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(title = title, onBack = onBack)

        if (songs.isEmpty()) {
            EmptyState(messageRes = emptyMessageRes)
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = MaterialTheme.spacing.huge),
        ) {
            item {
                CollectionHeader(
                    title = title,
                    coverUrl = coverUrl,
                    songCount = songs.size,
                    onPlayAll = onPlayAll,
                    onShuffle = onShuffle,
                )
            }
            items(items = songs, key = { it.id }) { song ->
                if (onRemove != null) {
                    SwipeToDeleteRow(onDelete = { onRemove(song) }) {
                        SongRow(song = song, onClick = { onSongClick(song) })
                    }
                } else {
                    SongRow(song = song, onClick = { onSongClick(song) })
                }
            }
        }
    }
}

@Composable
private fun CollectionHeader(
    title: String,
    coverUrl: String?,
    songCount: Int,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.screen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        if (coverUrl != null) {
            NetworkImage(
                url = coverUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(MaterialTheme.spacing.coverLarge)
                    .clip(AppCorners.card),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Text(
            text = pluralStringResource(R.plurals.song_count, songCount, songCount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            Button(onClick = onPlayAll) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = stringResource(R.string.action_play_all),
                    modifier = Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                )
            }
            OutlinedButton(onClick = onShuffle) {
                Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = stringResource(R.string.action_shuffle),
                    modifier = Modifier.padding(start = MaterialTheme.spacing.extraSmall),
                )
            }
        }
    }
}
