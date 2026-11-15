package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.repository.BookmarkRepository
import javax.inject.Inject

class SetUserRatingUseCase @Inject constructor(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(movieId: Int, rating: Int) {
        repository.setUserRating(movieId, rating)
    }
}
