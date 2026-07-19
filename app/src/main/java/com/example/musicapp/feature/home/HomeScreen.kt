package com.example.musicapp.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.HomeFeed
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.ArtistCard
import com.example.musicapp.ui.components.PlaylistCard
import com.example.musicapp.ui.components.SectionHeader
import com.example.musicapp.ui.components.ShimmerBox
import com.example.musicapp.ui.components.SongCard
import com.example.musicapp.ui.theme.AppCorners
import com.example.musicapp.ui.theme.spacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit = {},
    onPlaylistClick: (Playlist) -> Unit = {},
    onArtistClick: (Artist) -> Unit = {},
    onQuickLiked: () -> Unit = {},
    onQuickRecent: () -> Unit = {},
    onQuickPlaylists: () -> Unit = {},
    onQuickArtists: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        HomeSkeleton(modifier)
    } else {
        HomeContent(
            feed = state.feed,
            modifier = modifier,
            onSongClick = {
                viewModel.onSongSelected(it)
                onSongClick(it)
            },
            onPlaylistClick = onPlaylistClick,
            onArtistClick = onArtistClick,
            onQuickLiked = onQuickLiked,
            onQuickRecent = onQuickRecent,
            onQuickPlaylists = onQuickPlaylists,
            onQuickArtists = onQuickArtists,
        )
    }
}

@Composable
private fun HomeContent(
    feed: HomeFeed,
    onSongClick: (Song) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onQuickLiked: () -> Unit,
    onQuickRecent: () -> Unit,
    onQuickPlaylists: () -> Unit,
    onQuickArtists: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = MaterialTheme.spacing.small,
            bottom = MaterialTheme.spacing.huge,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sectionGap),
    ) {
        item { HomeCarousel(songs = feed.dailyPicks, onSongClick = onSongClick) }

        item {
            QuickActions(
                onLiked = onQuickLiked,
                onRecent = onQuickRecent,
                onPlaylists = onQuickPlaylists,
                onArtists = onQuickArtists,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
            )
        }

        item {
            SongSection(R.string.home_newest, feed.newest, onSongClick)
        }
        item {
            SongSection(R.string.home_most_popular, feed.mostPopular, onSongClick)
        }
        item {
            PlaylistSection(R.string.home_global_playlists, feed.globalPlaylists, onPlaylistClick)
        }
        item {
            PlaylistSection(R.string.home_local_playlists, feed.localPlaylists, onPlaylistClick)
        }
        item {
            ArtistSection(R.string.home_quick_top_artists, feed.topArtists, onArtistClick)
        }
    }
}

// ---- Sections ----

@Composable
private fun SongSection(
    @StringRes titleRes: Int,
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
) {
    if (songs.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        SectionHeader(
            title = stringResource(titleRes),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.screen),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
        ) {
            items(songs.size) { i ->
                val song = songs[i]
                SongCard(song = song, onClick = { onSongClick(song) })
            }
        }
    }
}

@Composable
private fun PlaylistSection(
    @StringRes titleRes: Int,
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
) {
    if (playlists.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        SectionHeader(
            title = stringResource(titleRes),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.screen),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
        ) {
            items(playlists.size) { i ->
                val playlist = playlists[i]
                PlaylistCard(playlist = playlist, onClick = { onPlaylistClick(playlist) })
            }
        }
    }
}

@Composable
private fun ArtistSection(
    @StringRes titleRes: Int,
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
) {
    if (artists.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        SectionHeader(
            title = stringResource(titleRes),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.screen),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.screen),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap),
        ) {
            items(artists.size) { i ->
                val artist = artists[i]
                ArtistCard(artist = artist, onClick = { onArtistClick(artist) })
            }
        }
    }
}

// ---- Quick actions ----

@Composable
private fun QuickActions(
    onLiked: () -> Unit,
    onRecent: () -> Unit,
    onPlaylists: () -> Unit,
    onArtists: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        QuickActionButton(Icons.Filled.FavoriteBorder, R.string.home_quick_liked, onLiked, Modifier.weight(1f))
        QuickActionButton(Icons.Filled.History, R.string.home_quick_recent, onRecent, Modifier.weight(1f))
        QuickActionButton(Icons.Filled.LibraryMusic, R.string.home_quick_my_playlists, onPlaylists, Modifier.weight(1f))
        QuickActionButton(Icons.Filled.Star, R.string.home_quick_top_artists, onArtists, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(AppCorners.card)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

// ---- Loading skeleton (Shimmer) ----

@Composable
private fun HomeSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(AppCorners.card),
        )
        repeat(2) {
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
                    .clip(AppCorners.pill),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.itemGap)) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(AppCorners.card),
                    )
                }
            }
        }
    }
}
