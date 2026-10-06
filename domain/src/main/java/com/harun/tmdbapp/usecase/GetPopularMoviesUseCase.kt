package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesUseCase(
    private val repository: Repository
) {
    operator fun invoke(): Flow<PagingData<Movie>> {
        return repository.getPopularMovies()
    }
}