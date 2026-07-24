package com.example.musicapp.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User
import com.example.musicapp.ui.components.ArtistRow
import com.example.musicapp.ui.components.EmptyState
import com.example.musicapp.ui.components.PlaylistCard
import com.example.musicapp.ui.components.SongRow
import com.example.musicapp.ui.components.UserRow
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit = {},
    onUserClick: (User) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val songs = viewModel.songResults.collectAsLazyPagingItems()
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.fillMaxSize()) {
        SearchField(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onClear = { viewModel.onQueryChange("") },
            onSearch = {
                viewModel.onSearchSubmitted()
                keyboard?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.screen, vertical = MaterialTheme.spacing.small),
        )

        FilterChipsRow(
            selected = state.filter,
            onSelect = viewModel::onFilterChange,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
        )

        Box(modifier = Modifier.weight(1f)) {
            if (state.query.isBlank()) {
                HistoryContent(
                    history = state.history,
                    onHistoryClick = viewModel::onHistoryClick,
                    onDelete = viewModel::onDeleteHistory,
                    onClearAll = viewModel::onClearHistory,
                )
            } else {
                ResultsContent(
                    state = state,
                    songs = songs,
                    onSongClick = {
                        viewModel.onSongSelected(it)
                        onSongClick(it)
                    },
                    onToggleLike = viewModel::onToggleLike,
                    onUserClick = onUserClick,
                    onToggleFollowUser = viewModel::onToggleFollowUser,
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        singleLine = true,
        shape = AppCorners.pill,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_cancel))
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    )
}

@Composable
private fun FilterChipsRow(
    selected: SearchFilter,
    onSelect: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        SearchFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(stringResource(filter.labelRes)) },
            )
        }
    }
}

@Composable
private fun HistoryContent(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit,
) {
    if (history.isEmpty()) {
        EmptyState(messageRes = R.string.empty_generic, icon = Icons.Outlined.History)
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = MaterialTheme.spacing.screen),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.search_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(onClick = onClearAll) {
                Text(stringResource(R.string.search_clear_history))
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history.size) { index ->
                val query = history[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryClick(query) }
                        .padding(horizontal = MaterialTheme.spacing.screen, vertical = MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = query,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = MaterialTheme.spacing.extraSmall),
                    )
                    IconButton(onClick = { onDelete(query) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.action_remove),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultsContent(
    state: SearchUiState,
    songs: androidx.paging.compose.LazyPagingItems<Song>,
    onSongClick: (Song) -> Unit,
    onToggleLike: (Song) -> Unit,
    onUserClick: (User) -> Unit,
    onToggleFollowUser: (User) -> Unit,
) {
    when (state.filter) {
        SearchFilter.ARTISTS -> {
            if (state.artists.isEmpty()) {
                EmptyState(messageRes = R.string.search_no_results, icon = Icons.Outlined.SearchOff)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.artists.size) { i ->
                        val artist = state.artists[i]
                        ArtistRow(artist = artist, onClick = { })
                    }
                }
            }
        }

        SearchFilter.USERS -> {
            if (state.users.isEmpty()) {
                EmptyState(messageRes = R.string.search_no_results, icon = Icons.Outlined.SearchOff)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = state.users, key = { it.id }) { user ->
                        UserRow(
                            user = user,
                            onClick = { onUserClick(user) },
                            trailing = {
                                OutlinedButton(
                                    onClick = { onToggleFollowUser(user) },
                                ) {
                                    Text(
                                        stringResource(
                                            if (user.isFollowed) R.string.action_unfollow
                                            else R.string.action_follow,
                                        ),
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }

        SearchFilter.PLAYLISTS -> {
            if (state.playlists.isEmpty()) {
                EmptyState(messageRes = R.string.search_no_results, icon = Icons.Outlined.SearchOff)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(MaterialTheme.spacing.screen),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
                ) {
                    items(state.playlists.size) { i ->
                        val playlist = state.playlists[i]
                        PlaylistCard(playlist = playlist, onClick = { }, width = androidx.compose.ui.unit.Dp.Unspecified)
                    }
                }
            }
        }

        else -> { // ALL and SONGS -> paged songs
            val refreshing = songs.loadState.refresh is LoadState.Loading
            if (!refreshing && songs.itemCount == 0) {
                EmptyState(messageRes = R.string.search_no_results, icon = Icons.Outlined.SearchOff)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(count = songs.itemCount) { index ->
                        val song = songs[index]
                        if (song != null) {
                            SongRow(
                                song = song,
                                onClick = { onSongClick(song) },
                                onToggleLike = { onToggleLike(song) },
                            )
                        }
                    }
                }
            }
        }
    }
}
