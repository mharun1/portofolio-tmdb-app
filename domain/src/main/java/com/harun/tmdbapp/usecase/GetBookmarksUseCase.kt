package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<BookmarkedMovie>> {
        return repository.getBookmarkedMovies()
    }
}
