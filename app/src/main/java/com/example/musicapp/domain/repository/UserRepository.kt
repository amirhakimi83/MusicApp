package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.User
import kotlinx.coroutines.flow.Flow

/** Current user profile, Premium status, and social graph (follows). */
interface UserRepository {
    fun getCurrentUser(): Flow<User>
    suspend fun updateAvatar(uri: String)
    suspend fun upgradeToPremium()

    /** Reset the mock session: clears Premium status and any custom avatar. */
    suspend fun logout()

    fun getFollowedUsers(): Flow<List<User>>
    fun getFollowedArtists(): Flow<List<Artist>>
    suspend fun toggleFollowUser(userId: String)
    suspend fun toggleFollowArtist(artistId: String)

    /** Everyone else on the platform, with live follow state — for the People/discover screen. */
    fun getAllOtherUsers(): Flow<List<User>>

    /** Public playlists of other users (for the social/discovery section). */
    fun getPublicPlaylists(): Flow<List<Playlist>>
    suspend fun getUserById(id: String): User?
}
