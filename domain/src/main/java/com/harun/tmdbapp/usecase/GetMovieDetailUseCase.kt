package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(id: Int): Result<MovieDetail> {
        return repository.getDetailMovie(id)
    }
}