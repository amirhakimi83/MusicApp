package com.example.musicapp.feature.artist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.ui.components.TrackCollectionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailState(
    val artist: Artist? = null,
    val songs: List<Song> = emptyList(),
)

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])

    private val _state = MutableStateFlow(ArtistDetailState())
    val state: StateFlow<ArtistDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = ArtistDetailState(
                artist = musicRepository.getArtistById(artistId),
                songs = musicRepository.getSongsByArtist(artistId),
            )
        }
    }
}

@Composable
fun ArtistDetailScreen(
    onBack: () -> Unit,
    onPlaySong: (Song) -> Unit,
    onPlayList: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val songs = state.songs

    TrackCollectionScreen(
        title = state.artist?.name.orEmpty(),
        coverUrl = state.artist?.imageUrl,
        songs = songs,
        onBack = onBack,
        onPlayAll = { if (songs.isNotEmpty()) onPlayList(songs, 0) },
        onShuffle = { if (songs.isNotEmpty()) onPlayList(songs.shuffled(), 0) },
        onSongClick = onPlaySong,
        emptyMessageRes = R.string.empty_generic,
        modifier = modifier,
    )
}
