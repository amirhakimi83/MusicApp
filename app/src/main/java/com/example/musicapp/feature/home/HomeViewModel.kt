package com.example.musicapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.HomeFeed
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.LibraryRepository
import com.example.musicapp.domain.usecase.GetHomeFeedUseCase
import com.example.musicapp.domain.usecase.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Single immutable state object for the Home screen (UDF). */
data class HomeUiState(
    val isLoading: Boolean = true,
    val feed: HomeFeed = HomeFeed(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeFeed: GetHomeFeedUseCase,
    private val toggleLike: ToggleLikeUseCase,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getHomeFeed()
        .map { HomeUiState(isLoading = false, feed = it) }
        .catch { emit(HomeUiState(isLoading = false, error = it.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(isLoading = true),
        )

    fun onToggleLike(song: Song) = viewModelScope.launch {
        toggleLike(song)
    }

    /** Records a play in history. Actual playback is wired in Step 9. */
    fun onSongSelected(song: Song) = viewModelScope.launch {
        libraryRepository.addRecentlyPlayed(song)
    }
}
