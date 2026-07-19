package com.example.musicapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.ui.theme.spacing

/** Horizontal artist list item with circular avatar + follower count. */
@Composable
fun ArtistRow(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            url = artist.imageUrl,
            contentDescription = artist.name,
            placeholderIcon = Icons.Filled.Person,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.format_follower_count, artist.followerCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        trailing?.invoke(this)
    }
}
