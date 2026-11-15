package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserRatingUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    operator fun invoke(movieId: Int): Flow<Int?> {
        return repository.getUserRating(movieId)
    }
}
