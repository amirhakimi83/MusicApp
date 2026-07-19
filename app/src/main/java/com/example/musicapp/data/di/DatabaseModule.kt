package com.example.musicapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.musicapp.data.local.ChatDao
import com.example.musicapp.data.local.DownloadDao
import com.example.musicapp.data.local.LibraryDao
import com.example.musicapp.data.local.MusicDatabase
import com.example.musicapp.data.local.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase =
        Room.databaseBuilder(context, MusicDatabase::class.java, MusicDatabase.NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideLibraryDao(db: MusicDatabase): LibraryDao = db.libraryDao()

    @Provides
    fun provideDownloadDao(db: MusicDatabase): DownloadDao = db.downloadDao()

    @Provides
    fun provideSearchHistoryDao(db: MusicDatabase): SearchHistoryDao = db.searchHistoryDao()

    @Provides
    fun provideChatDao(db: MusicDatabase): ChatDao = db.chatDao()
}
