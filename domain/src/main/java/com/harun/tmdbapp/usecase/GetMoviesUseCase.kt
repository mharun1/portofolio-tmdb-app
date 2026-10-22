package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(
    private val repository: Repository,
) {
    operator fun invoke(category: MovieCategory): Flow<PagingData<Movie>> {
        return repository.getMovies(category)
    }
}
