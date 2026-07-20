package com.example.musicapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.musicapp.core.di.IoDispatcher
import com.example.musicapp.data.datastore.PreferenceKeys
import com.example.musicapp.data.mock.MockCatalog
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val io: CoroutineDispatcher,
) : UserRepository {

    // Social graph kept in memory for the mock backend.
    private val followedUserIds =
        MutableStateFlow(MockCatalog.otherUsers.take(2).map { it.id }.toSet())
    private val followedArtistIds =
        MutableStateFlow(MockCatalog.artists.take(3).map { it.id }.toSet())

    override fun getCurrentUser(): Flow<User> = dataStore.data.map { prefs ->
        MockCatalog.currentUser.copy(
            isPremium = prefs[PreferenceKeys.IS_PREMIUM] ?: false,
            avatarUrl = prefs[PreferenceKeys.AVATAR_URI] ?: MockCatalog.currentUser.avatarUrl,
        )
    }.flowOn(io)

    override suspend fun updateAvatar(uri: String) {
        dataStore.edit { it[PreferenceKeys.AVATAR_URI] = uri }
    }

    override suspend fun upgradeToPremium() {
        dataStore.edit { it[PreferenceKeys.IS_PREMIUM] = true }
    }

    override suspend fun logout() {
        dataStore.edit {
            it.remove(PreferenceKeys.IS_PREMIUM)
            it.remove(PreferenceKeys.AVATAR_URI)
        }
    }

    override fun getFollowedUsers(): Flow<List<User>> = followedUserIds.map { ids ->
        MockCatalog.otherUsers.filter { it.id in ids }.map { it.copy(isFollowed = true) }
    }.flowOn(io)

    override fun getFollowedArtists(): Flow<List<Artist>> = followedArtistIds.map { ids ->
        MockCatalog.artists.filter { it.id in ids }.map { it.copy(isFollowed = true) }
    }.flowOn(io)

    override suspend fun toggleFollowUser(userId: String) {
        followedUserIds.update { if (userId in it) it - userId else it + userId }
    }

    override suspend fun toggleFollowArtist(artistId: String) {
        followedArtistIds.update { if (artistId in it) it - artistId else it + artistId }
    }

    override fun getPublicPlaylists(): Flow<List<Playlist>> = flow {
        emit(MockCatalog.globalPlaylists.map { it.copy(isPublic = true) })
    }.flowOn(io)

    override suspend fun getUserById(id: String): User? =
        withContext(io) { MockCatalog.userById(id) }
}
