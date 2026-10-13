package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository

class GetMovieDetailUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(id: Int): Result<MovieDetail> {
        return repository.getDetailMovie(id)
    }
}