package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.repository.Repository

class GetNowPlayingMovieUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getNowPlayingMovie()
    }
}