package com.harun.tmdbapp.repository

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getPopularMovies(): Flow<PagingData<Movie>>
    suspend fun getDetailMovie(id: Int): MovieDetail
}