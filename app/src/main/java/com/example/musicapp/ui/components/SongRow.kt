package com.example.musicapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing

/**
 * Horizontal song list item: cover + title/artist + optional like toggle and
 * trailing slot. Reused across Search, Downloads, playlist detail, etc.
 */
@Composable
fun SongRow(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleLike: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.screen,
                vertical = MaterialTheme.spacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
    ) {
        NetworkImage(
            url = song.coverImageUrl,
            contentDescription = song.title,
            modifier = Modifier
                .size(MaterialTheme.spacing.coverSmall)
                .clip(AppCorners.cover),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
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
        if (onToggleLike != null) {
            IconButton(onClick = onToggleLike) {
                Icon(
                    imageVector = if (song.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(
                        if (song.isLiked) R.string.action_unlike else R.string.action_like,
                    ),
                    tint = if (song.isLiked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke(this)
    }
}
