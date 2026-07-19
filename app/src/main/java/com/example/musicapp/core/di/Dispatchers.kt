package com.example.musicapp.core.di

import javax.inject.Qualifier

/**
 * Qualifiers for injecting specific [kotlinx.coroutines.CoroutineDispatcher]s,
 * so repositories/use-cases never touch the main thread for I/O work.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

/** Application-lifetime [kotlinx.coroutines.CoroutineScope] for repository work. */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationScope
