package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(movie: BookmarkedMovie) {
        repository.toggleBookmark(movie)
    }
}
