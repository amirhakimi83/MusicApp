package com.example.musicapp.data.repository

import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.local.SearchHistoryDao
import com.example.musicapp.data.local.SearchHistoryEntity
import com.example.musicapp.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: SearchHistoryDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : SearchHistoryRepository {

    override fun getHistory(): Flow<List<String>> = dao.observeHistory().flowOn(io)

    override suspend fun addQuery(query: String) = withContext(io) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            dao.insert(SearchHistoryEntity(trimmed, System.currentTimeMillis()))
        }
    }

    override suspend fun removeQuery(query: String) = withContext(io) { dao.delete(query) }

    override suspend fun clearHistory() = withContext(io) { dao.clear() }
}
