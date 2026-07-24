package com.example.musicapp.feature.search

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.LibraryRepository
import com.example.musicapp.domain.repository.SearchHistoryRepository
import com.example.musicapp.domain.repository.SearchRepository
import com.example.musicapp.domain.repository.UserRepository
import com.example.musicapp.domain.usecase.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Which kind of results the user is filtering to. */
enum class SearchFilter(@StringRes val labelRes: Int) {
    ALL(R.string.search_filter_all),
    SONGS(R.string.search_filter_songs),
    ARTISTS(R.string.search_filter_artists),
    PLAYLISTS(R.string.search_filter_playlists),
    USERS(R.string.search_filter_users),
}

data class SearchUiState(
    val query: String = "",
    val filter: SearchFilter = SearchFilter.ALL,
    val history: List<String> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val users: List<User> = emptyList(),
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val libraryRepository: LibraryRepository,
    private val userRepository: UserRepository,
    private val toggleLike: ToggleLikeUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow(SearchFilter.ALL)

    // Debounced so we don't fire a query on every keystroke.
    private val debouncedQuery = _query
        .debounce(350)
        .distinctUntilChanged()

    /** Paged song results (Paging 3), cached across config changes. */
    val songResults: Flow<PagingData<Song>> = debouncedQuery
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(PagingData.empty())
            else searchRepository.searchSongs(query)
        }
        .cachedIn(viewModelScope)

    private val artistResults = debouncedQuery.flatMapLatest { searchRepository.searchArtists(it) }
    private val playlistResults = debouncedQuery.flatMapLatest { searchRepository.searchPlaylists(it) }
    private val userResults = debouncedQuery.flatMapLatest { userRepository.searchUsers(it) }

    private val baseState = combine(
        _query,
        _filter,
        searchHistoryRepository.getHistory(),
        artistResults,
        playlistResults,
    ) { query, filter, history, artists, playlists ->
        SearchUiState(
            query = query,
            filter = filter,
            history = history,
            artists = artists,
            playlists = playlists,
        )
    }

    val uiState: StateFlow<SearchUiState> = combine(baseState, userResults) { state, users ->
        state.copy(users = users)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(),
    )

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onFilterChange(filter: SearchFilter) {
        _filter.value = filter
    }

    fun onSearchSubmitted() = commitHistory(_query.value)

    fun onHistoryClick(query: String) {
        _query.value = query
    }

    fun onDeleteHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.removeQuery(query)
    }

    fun onClearHistory() = viewModelScope.launch {
        searchHistoryRepository.clearHistory()
    }

    fun onToggleLike(song: Song) = viewModelScope.launch {
        toggleLike(song)
    }

    fun onToggleFollowUser(user: User) = viewModelScope.launch {
        userRepository.toggleFollowUser(user.id)
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        libraryRepository.addRecentlyPlayed(song)
        commitHistory(_query.value)
    }

    private fun commitHistory(query: String) = viewModelScope.launch {
        if (query.isNotBlank()) searchHistoryRepository.addQuery(query)
    }
}
