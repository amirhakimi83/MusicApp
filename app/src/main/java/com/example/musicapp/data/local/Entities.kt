package com.example.musicapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room entities. Song-bearing tables store a denormalized snapshot of the
 * song so liked/recent/downloaded items remain available fully offline.
 */

@Entity(tableName = "liked_songs")
data class LikedSongEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val coverImageUrl: String,
    val audioUrl: String,
    val durationMs: Long,
    val album: String?,
    val likedAt: Long,
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val coverImageUrl: String,
    val audioUrl: String,
    val durationMs: Long,
    val album: String?,
    val playedAt: Long,
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val coverImageUrl: String,
    val audioUrl: String,
    val durationMs: Long,
    val album: String?,
    val localPath: String,
    val downloadedAt: Long,
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long,
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val participantId: String,
    val participantName: String,
    val participantUsername: String,
    val participantAvatar: String?,
    val lastMessageText: String?,
    val lastTimestamp: Long,
    val unreadCount: Int,
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String?,
    // Shared-song snapshot (null when the message is plain text)
    val sharedSongId: String?,
    val sharedSongTitle: String?,
    val sharedSongArtist: String?,
    val sharedSongCover: String?,
    val sharedSongAudio: String?,
    val timestamp: Long,
    val status: String,
    val isMine: Boolean,
)
