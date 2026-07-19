package com.example.musicapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LikedSongEntity::class,
        RecentlyPlayedEntity::class,
        DownloadEntity::class,
        SearchHistoryEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun chatDao(): ChatDao

    companion object {
        const val NAME = "melodia.db"
    }
}
