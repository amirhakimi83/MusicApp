package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.LibraryRepository
import javax.inject.Inject

/** Adds or removes a song from the user's liked list. */
class ToggleLikeUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(song: Song) = libraryRepository.toggleLike(song)
}
