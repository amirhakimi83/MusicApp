package com.example.musicapp.ui

import androidx.compose.foundation.layout.Column
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
import com.example.musicapp.ui.components.MelodiaTopBar
import com.example.musicapp.ui.navigation.MelodiaBottomBar
import com.example.musicapp.ui.navigation.MelodiaNavHost
import com.example.musicapp.ui.navigation.TopLevelDestination
import com.example.musicapp.ui.player.MiniPlayerSlot

/**
 * Root of the app UI: a Scaffold hosting the top bar, the tab NavHost, the
 * persistent mini-player slot, and the bottom navigation bar. The bars only
 * show on top-level tab destinations.
 */
@Composable
fun MelodiaApp(
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val user by appViewModel.currentUser.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute in TopLevelDestination.routes

    fun navigateToTab(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            // Avoid building up a large back stack; keep a single copy of each tab.
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isTopLevel) {
                MelodiaTopBar(
                    avatarUrl = user?.avatarUrl,
                    // Notifications & Settings destinations are wired in later steps.
                    onNotificationsClick = { },
                    onSettingsClick = { },
                    onProfileClick = { navigateToTab(TopLevelDestination.PROFILE) },
                )
            }
        },
        bottomBar = {
            if (isTopLevel) {
                Column {
                    MiniPlayerSlot()
                    MelodiaBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = ::navigateToTab,
                    )
                }
            }
        },
    ) { innerPadding ->
        MelodiaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
