package com.example.musicapp.feature.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.DownloadSort
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.DownloadRepository
import com.example.musicapp.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _sort = MutableStateFlow(DownloadSort.RECENT)
    val sort: StateFlow<DownloadSort> = _sort.asStateFlow()

    val downloads: StateFlow<List<Song>> = _sort
        .flatMapLatest { downloadRepository.getDownloadedSongs(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setSort(sort: DownloadSort) {
        _sort.value = sort
    }

    fun remove(song: Song) = viewModelScope.launch {
        downloadRepository.removeDownload(song.id)
    }

    fun onSongSelected(song: Song) = viewModelScope.launch {
        libraryRepository.addRecentlyPlayed(song)
    }
}
