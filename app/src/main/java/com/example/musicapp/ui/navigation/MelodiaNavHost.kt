package com.example.musicapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicapp.domain.model.Song
import com.example.musicapp.feature.artist.ArtistDetailScreen
import com.example.musicapp.feature.artist.FollowedArtistsScreen
import com.example.musicapp.feature.chat.ChatScreen
import com.example.musicapp.feature.chat.MessagesScreen
import com.example.musicapp.feature.downloads.DownloadsScreen
import com.example.musicapp.feature.home.HomeScreen
import com.example.musicapp.feature.library.LikedSongsScreen
import com.example.musicapp.feature.library.RecentlyPlayedScreen
import com.example.musicapp.feature.playlists.PlaylistDetailScreen
import com.example.musicapp.feature.playlists.PlaylistsScreen
import com.example.musicapp.feature.profile.ProfileScreen
import com.example.musicapp.feature.search.SearchScreen
import com.example.musicapp.feature.settings.SettingsScreen

@Composable
fun MelodiaNavHost(
    navController: NavHostController,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun openPlaylist(id: String) = navController.navigate(Routes.playlistDetail(id))
    fun openArtist(id: String) = navController.navigate(Routes.artistDetail(id))
    fun back() = navController.popBackStack()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onSongClick = onPlaySong,
                onPlaylistClick = { openPlaylist(it.id) },
                onArtistClick = { openArtist(it.id) },
                onQuickLiked = { navController.navigate(Routes.LIKED) },
                onQuickRecent = { navController.navigate(Routes.RECENT) },
                onQuickPlaylists = { navController.navigate(Routes.PLAYLISTS) },
                onQuickArtists = { navController.navigate(Routes.ARTISTS) },
            )
        }
        composable(Routes.SEARCH) { SearchScreen(onSongClick = onPlaySong) }
        composable(Routes.DOWNLOADS) { DownloadsScreen(onSongClick = onPlaySong) }
        composable(Routes.PLAYLISTS) {
            PlaylistsScreen(onPlaylistClick = { openPlaylist(it.id) })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenLiked = { navController.navigate(Routes.LIKED) },
                onOpenRecent = { navController.navigate(Routes.RECENT) },
                onOpenArtists = { navController.navigate(Routes.ARTISTS) },
                onOpenMessages = { navController.navigate(Routes.MESSAGES) },
            )
        }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { back() }) }

        composable(Routes.MESSAGES) {
            MessagesScreen(
                onBack = { back() },
                onOpenChat = { navController.navigate(Routes.chat(it)) },
            )
        }
        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument(Routes.ARG_CONVERSATION_ID) { type = NavType.StringType }),
        ) {
            ChatScreen(onBack = { back() }, onSongClick = onPlaySong)
        }

        composable(Routes.LIKED) {
            LikedSongsScreen(onBack = { back() }, onPlaySong = onPlaySong, onPlayList = onPlayList)
        }
        composable(Routes.RECENT) {
            RecentlyPlayedScreen(onBack = { back() }, onPlaySong = onPlaySong, onPlayList = onPlayList)
        }
        composable(Routes.ARTISTS) {
            FollowedArtistsScreen(onBack = { back() }, onArtistClick = { openArtist(it.id) })
        }
        composable(
            route = Routes.PLAYLIST_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_PLAYLIST_ID) { type = NavType.StringType }),
        ) {
            PlaylistDetailScreen(onBack = { back() }, onPlaySong = onPlaySong, onPlayList = onPlayList)
        }
        composable(
            route = Routes.ARTIST_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_ARTIST_ID) { type = NavType.StringType }),
        ) {
            ArtistDetailScreen(onBack = { back() }, onPlaySong = onPlaySong, onPlayList = onPlayList)
        }
    }
}
