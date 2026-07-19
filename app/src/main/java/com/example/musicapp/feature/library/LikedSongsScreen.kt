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
import com.example.musicapp.domain.usecase.ToggleLikeUseCase
import com.example.musicapp.ui.components.TrackCollectionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedViewModel @Inject constructor(
    libraryRepository: LibraryRepository,
    private val toggleLike: ToggleLikeUseCase,
) : ViewModel() {

    val songs: StateFlow<List<Song>> = libraryRepository.getLikedSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun unlike(song: Song) = viewModelScope.launch { toggleLike(song) }
}

@Composable
fun LikedSongsScreen(
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LikedViewModel = hiltViewModel(),
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    TrackCollectionScreen(
        title = stringResource(R.string.home_quick_liked),
        songs = songs,
        onBack = onBack,
        onPlayAll = { if (songs.isNotEmpty()) onPlayList(songs, 0) },
        onShuffle = { if (songs.isNotEmpty()) onPlayList(songs.shuffled(), 0) },
        onSongClick = onPlaySong,
        onRemove = viewModel::unlike,
        emptyMessageRes = R.string.empty_liked,
        modifier = modifier,
    )
}
