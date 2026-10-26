package com.harun.tmdbapp.repository

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getMovies(category: MovieCategory): Flow<PagingData<Movie>>
    suspend fun getDetailMovie(id: Int): Result<MovieDetail>
    suspend fun getNowPlayingMovie(): Result<List<Movie>>
}
