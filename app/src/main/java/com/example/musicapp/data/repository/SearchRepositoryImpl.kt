package com.example.musicapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    @IoDispatcher private val io: CoroutineDispatcher,
) : SearchRepository {

    override fun searchSongs(query: String): Flow<PagingData<Song>> =
        Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = { SongSearchPagingSource(query) },
        ).flow

    override fun searchArtists(query: String): Flow<List<Artist>> = flow {
        emit(
            if (query.isBlank()) emptyList()
            else MockCatalog.artists.filter { it.name.contains(query, ignoreCase = true) },
        )
    }.flowOn(io)

    override fun searchPlaylists(query: String): Flow<List<Playlist>> = flow {
        emit(
            if (query.isBlank()) emptyList()
            else MockCatalog.allPlaylists.filter { it.title.contains(query, ignoreCase = true) },
        )
    }.flowOn(io)
}

/** Offset-keyed paging over the in-memory catalog filtered by [query]. */
private class SongSearchPagingSource(
    private val query: String,
) : PagingSource<Int, Song>() {

    private val results: List<Song> =
        if (query.isBlank()) emptyList()
        else MockCatalog.songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.artistName.contains(query, ignoreCase = true)
        }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val start = params.key ?: 0
        val end = minOf(start + params.loadSize, results.size)
        val data = if (start < end) results.subList(start, end) else emptyList()
        return LoadResult.Page(
            data = data,
            prevKey = if (start == 0) null else maxOf(0, start - params.loadSize),
            nextKey = if (end >= results.size) null else end,
        )
    }
}
