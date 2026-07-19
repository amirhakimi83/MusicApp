package com.example.musicapp.feature.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.DownloadSort
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.EmptyState
import com.example.musicapp.ui.components.SongRow
import com.example.musicapp.ui.theme.spacing

@Composable
fun DownloadsScreen(
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit = {},
    viewModel: DownloadsViewModel = hiltViewModel(),
) {
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val sort by viewModel.sort.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Header(sort = sort, onSortChange = viewModel::setSort)

        if (downloads.isEmpty()) {
            EmptyState(
                messageRes = R.string.downloads_empty,
                icon = Icons.Outlined.CloudDownload,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = downloads, key = { it.id }) { song ->
                    SwipeableSongRow(
                        song = song,
                        onClick = {
                            viewModel.onSongSelected(song)
                            onSongClick(song)
                        },
                        onDismiss = { viewModel.remove(song) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    sort: DownloadSort,
    onSortChange: (DownloadSort) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.screen, vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.downloads_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Box {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.Sort, contentDescription = stringResource(R.string.downloads_sort))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sortOptions.forEach { (option, labelRes) ->
                    DropdownMenuItem(
                        text = { Text(stringResource(labelRes)) },
                        onClick = {
                            onSortChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeableSongRow(
    song: Song,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDismiss()
                true
            } else {
                false
            }
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = MaterialTheme.spacing.large),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SongRow(song = song, onClick = onClick)
        }
    }
}

private val sortOptions = listOf(
    DownloadSort.RECENT to R.string.downloads_sort_recent,
    DownloadSort.TITLE to R.string.downloads_sort_title,
    DownloadSort.ARTIST to R.string.downloads_sort_artist,
)
