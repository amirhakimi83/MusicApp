package com.example.musicapp.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.LibraryRepository
import com.example.musicapp.ui.components.TrackCollectionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    libraryRepository: LibraryRepository,
) : ViewModel() {

    val songs: StateFlow<List<Song>> = libraryRepository.getRecentlyPlayed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun RecentlyPlayedScreen(
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentViewModel = hiltViewModel(),
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    TrackCollectionScreen(
        title = stringResource(R.string.home_quick_recent),
        songs = songs,
        onBack = onBack,
        onPlayAll = { if (songs.isNotEmpty()) onPlayList(songs, 0) },
        onShuffle = { if (songs.isNotEmpty()) onPlayList(songs.shuffled(), 0) },
        onSongClick = onPlaySong,
        emptyMessageRes = R.string.empty_generic,
        modifier = modifier,
    )
}
