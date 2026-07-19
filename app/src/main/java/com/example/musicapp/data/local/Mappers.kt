package com.example.musicapp.data.local

import com.example.musicapp.domain.model.ChatMessage
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.MessageStatus
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User

// ---- Song <-> entities ----

fun LikedSongEntity.toSong() = Song(
    id = songId, title = title, artistName = artistName, artistId = artistId,
    coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
    album = album, isLiked = true,
)

fun Song.toLikedEntity(likedAt: Long = System.currentTimeMillis()) = LikedSongEntity(
    songId = id, title = title, artistName = artistName, artistId = artistId,
    coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
    album = album, likedAt = likedAt,
)

fun RecentlyPlayedEntity.toSong() = Song(
    id = songId, title = title, artistName = artistName, artistId = artistId,
    coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
    album = album,
)

fun Song.toRecentEntity(playedAt: Long = System.currentTimeMillis()) = RecentlyPlayedEntity(
    songId = id, title = title, artistName = artistName, artistId = artistId,
    coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
    album = album, playedAt = playedAt,
)

fun DownloadEntity.toSong() = Song(
    id = songId, title = title, artistName = artistName, artistId = artistId,
    coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
    album = album, isDownloaded = true, localFilePath = localPath,
)

fun Song.toDownloadEntity(localPath: String, at: Long = System.currentTimeMillis()) =
    DownloadEntity(
        songId = id, title = title, artistName = artistName, artistId = artistId,
        coverImageUrl = coverImageUrl, audioUrl = audioUrl, durationMs = durationMs,
        album = album, localPath = localPath, downloadedAt = at,
    )

// ---- Chat <-> entities ----

fun ConversationEntity.toConversation() = Conversation(
    id = id,
    participant = User(
        id = participantId,
        name = participantName,
        username = participantUsername,
        avatarUrl = participantAvatar,
    ),
    lastMessage = null,
    unreadCount = unreadCount,
)

fun MessageEntity.toMessage(): ChatMessage = ChatMessage(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    text = text,
    sharedSong = sharedSongId?.let {
        Song(
            id = it,
            title = sharedSongTitle.orEmpty(),
            artistName = sharedSongArtist.orEmpty(),
            artistId = "",
            coverImageUrl = sharedSongCover.orEmpty(),
            audioUrl = sharedSongAudio.orEmpty(),
        )
    },
    timestamp = timestamp,
    status = runCatching { MessageStatus.valueOf(status) }.getOrDefault(MessageStatus.SENT),
    isMine = isMine,
)

fun ChatMessage.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    text = text,
    sharedSongId = sharedSong?.id,
    sharedSongTitle = sharedSong?.title,
    sharedSongArtist = sharedSong?.artistName,
    sharedSongCover = sharedSong?.coverImageUrl,
    sharedSongAudio = sharedSong?.audioUrl,
    timestamp = timestamp,
    status = status.name,
    isMine = isMine,
)
