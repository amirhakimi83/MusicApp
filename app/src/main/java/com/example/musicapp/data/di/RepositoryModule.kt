package com.example.musicapp.data.di

import com.example.musicapp.data.repository.ChatRepositoryImpl
import com.example.musicapp.data.repository.DownloadRepositoryImpl
import com.example.musicapp.data.repository.LibraryRepositoryImpl
import com.example.musicapp.data.repository.MusicRepositoryImpl
import com.example.musicapp.data.repository.SearchHistoryRepositoryImpl
import com.example.musicapp.data.repository.SearchRepositoryImpl
import com.example.musicapp.data.remote.ChatSocket
import com.example.musicapp.data.remote.FakeChatSocket
import com.example.musicapp.data.repository.SettingsRepositoryImpl
import com.example.musicapp.data.repository.UserRepositoryImpl
import com.example.musicapp.domain.repository.ChatRepository
import com.example.musicapp.domain.repository.DownloadRepository
import com.example.musicapp.domain.repository.LibraryRepository
import com.example.musicapp.domain.repository.MusicRepository
import com.example.musicapp.domain.repository.SearchHistoryRepository
import com.example.musicapp.domain.repository.SearchRepository
import com.example.musicapp.domain.repository.SettingsRepository
import com.example.musicapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Binds each repository interface to its implementation (scope on the impls). */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMusicRepository(impl: MusicRepositoryImpl): MusicRepository

    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindSearchHistoryRepository(impl: SearchHistoryRepositoryImpl): SearchHistoryRepository

    @Binds
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

    @Binds
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    abstract fun bindChatSocket(impl: FakeChatSocket): ChatSocket

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
