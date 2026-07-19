package com.example.musicapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.example.musicapp.R

/** Route constants for the whole app (top-level tabs + detail screens). */
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val DOWNLOADS = "downloads"
    const val PLAYLISTS = "playlists"
    const val PROFILE = "profile"

    // Detail / full-screen routes
    const val SETTINGS = "settings"
    const val PLAYER = "player"
    const val MESSAGES = "messages"
    const val LIKED = "liked"
    const val RECENT = "recent"
    const val ARTISTS = "artists"

    const val ARG_PLAYLIST_ID = "playlistId"
    const val ARG_ARTIST_ID = "artistId"
    const val PLAYLIST_DETAIL = "playlist/{$ARG_PLAYLIST_ID}"
    const val ARTIST_DETAIL = "artist/{$ARG_ARTIST_ID}"

    fun playlistDetail(id: String) = "playlist/$id"
    fun artistDetail(id: String) = "artist/$id"
}

/** The five bottom-navigation tabs. */
enum class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(Routes.HOME, R.string.tab_home, Icons.Filled.Home, Icons.Outlined.Home),
    SEARCH(Routes.SEARCH, R.string.tab_search, Icons.Filled.Search, Icons.Outlined.Search),
    DOWNLOADS(Routes.DOWNLOADS, R.string.tab_downloads, Icons.Filled.Download, Icons.Outlined.Download),
    PLAYLISTS(Routes.PLAYLISTS, R.string.tab_playlists, Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
    PROFILE(Routes.PROFILE, R.string.tab_profile, Icons.Filled.Person, Icons.Outlined.Person),
    ;

    companion object {
        val routes: Set<String> = entries.map { it.route }.toSet()
    }
}
