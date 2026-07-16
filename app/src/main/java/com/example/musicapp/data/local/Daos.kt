package com.example.musicapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
/**
 * Data Access Objects (DAOs) for local database operations.
 * Handles local caching, offline availability of user library, and fast data retrieval.
 */
@Dao
interface LibraryDao {

    // ---- Liked ----
    @Query("SELECT * FROM liked_songs ORDER BY likedAt DESC")
    fun observeLiked(): Flow<List<LikedSongEntity>>

    @Query("SELECT songId FROM liked_songs")
    fun observeLikedIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE songId = :songId)")
    fun isLiked(songId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE songId = :songId)")
    suspend fun isLikedOnce(songId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun like(song: LikedSongEntity)

    @Query("DELETE FROM liked_songs WHERE songId = :songId")
    suspend fun unlike(songId: String)

    // ---- Recently played ----
    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 50")
    fun observeRecent(): Flow<List<RecentlyPlayedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(song: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played")
    suspend fun clearRecent()
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun observeAllByRecent(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads ORDER BY title ASC")
    fun observeAllByTitle(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads ORDER BY artistName ASC")
    fun observeAllByArtist(): Flow<List<DownloadEntity>>

    @Query("SELECT downloadedAt FROM downloads WHERE songId = :songId LIMIT 1")
    fun observeExists(songId: String): Flow<Long?>

    @Query("SELECT localPath FROM downloads WHERE songId = :songId LIMIT 1")
    suspend fun getLocalPath(songId: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE songId = :songId")
    suspend fun delete(songId: String)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT `query` FROM search_history ORDER BY searchedAt DESC LIMIT 10")
    fun observeHistory(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clear()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun clearUnread(conversationId: String)

    @Query("UPDATE conversations SET lastMessageText = :text, lastTimestamp = :timestamp WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, text: String, timestamp: Long)

    @Query("UPDATE conversations SET unreadCount = unreadCount + 1 WHERE id = :conversationId")
    suspend fun incrementUnread(conversationId: String)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: String)

    @Query("UPDATE messages SET status = :status WHERE conversationId = :conversationId AND isMine = 1")
    suspend fun markMineAs(conversationId: String, status: String)
}
