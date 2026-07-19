package com.example.musicapp.ui.navigation

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.domain.model.Song
import com.example.musicapp.feature.downloads.DownloadsScreen
import com.example.musicapp.feature.home.HomeScreen
import com.example.musicapp.feature.playlists.PlaylistsScreen
import com.example.musicapp.feature.profile.ProfileScreen
import com.example.musicapp.feature.search.SearchScreen

@Composable
fun MelodiaNavHost(
    navController: NavHostController,
    onPlaySong: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) { HomeScreen(onSongClick = onPlaySong) }
        composable(Routes.SEARCH) { SearchScreen(onSongClick = onPlaySong) }
        composable(Routes.DOWNLOADS) { DownloadsScreen() }
        composable(Routes.PLAYLISTS) { PlaylistsScreen() }
        composable(Routes.PROFILE) { ProfileScreen() }
    }
}
