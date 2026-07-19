package com.example.musicapp.feature.playlists

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.ui.components.PlaylistCard
import com.example.musicapp.ui.components.SectionHeader
import com.example.musicapp.ui.theme.spacing

@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    onPlaylistClick: (Playlist) -> Unit = {},
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(MaterialTheme.spacing.screen),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
    ) {
        categorySection(R.string.playlists_world_music, state.world, onPlaylistClick)
        categorySection(R.string.playlists_local_music, state.local, onPlaylistClick)
        categorySection(R.string.playlists_my_playlists, state.user, onPlaylistClick)
    }
}

private fun LazyGridScope.categorySection(
    @StringRes titleRes: Int,
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
) {
    if (playlists.isEmpty()) return
    item(span = { GridItemSpan(maxLineSpan) }) {
        SectionHeader(title = stringResource(titleRes))
    }
    items(items = playlists, key = { it.id }) { playlist ->
        PlaylistCard(
            playlist = playlist,
            onClick = { onPlaylistClick(playlist) },
            width = Dp.Unspecified,
        )
    }
}
