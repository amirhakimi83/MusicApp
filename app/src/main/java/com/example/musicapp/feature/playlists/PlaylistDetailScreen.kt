package com.example.musicapp.feature.playlists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.ui.components.TrackCollectionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val playlistId: String = checkNotNull(savedStateHandle["playlistId"])

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    init {
        viewModelScope.launch {
            _playlist.value = musicRepository.getPlaylistById(playlistId)
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
) {
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()
    val songs = playlist?.songs ?: emptyList()

    TrackCollectionScreen(
        title = playlist?.title.orEmpty(),
        coverUrl = playlist?.coverImageUrl,
        songs = songs,
        onBack = onBack,
        onPlayAll = { if (songs.isNotEmpty()) onPlayList(songs, 0) },
        onShuffle = { if (songs.isNotEmpty()) onPlayList(songs.shuffled(), 0) },
        onSongClick = onPlaySong,
        emptyMessageRes = R.string.empty_generic,
        modifier = modifier,
    )
}
