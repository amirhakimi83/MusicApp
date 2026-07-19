package com.example.musicapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.feature.player.PlayerOverlay
import com.example.musicapp.feature.player.PlayerViewModel
import com.example.musicapp.ui.components.MelodiaTopBar
import com.example.musicapp.ui.navigation.MelodiaBottomBar
import com.example.musicapp.ui.navigation.MelodiaNavHost
import com.example.musicapp.ui.navigation.TopLevelDestination

/**
 * Root of the app UI: a Scaffold (top bar + tab NavHost + bottom navigation)
 * with the player overlay drawn on top (mini-player / full Now Playing).
 */
@Composable
fun MelodiaApp(
    appViewModel: AppViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val user by appViewModel.currentUser.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute in TopLevelDestination.routes

    fun navigateToTab(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (isTopLevel) {
                    MelodiaTopBar(
                        avatarUrl = user?.avatarUrl,
                        onNotificationsClick = { },
                        onSettingsClick = { },
                        onProfileClick = { navigateToTab(TopLevelDestination.PROFILE) },
                    )
                }
            },
            bottomBar = {
                if (isTopLevel) {
                    MelodiaBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = ::navigateToTab,
                    )
                }
            },
        ) { innerPadding ->
            MelodiaNavHost(
                navController = navController,
                onPlaySong = { playerViewModel.play(it) },
                onPlayList = { songs, index -> playerViewModel.play(songs, index) },
                modifier = Modifier.padding(innerPadding),
            )
        }

        // Player layer (mini-player + full Now Playing) drawn above everything.
        PlayerOverlay(viewModel = playerViewModel)
    }
}
