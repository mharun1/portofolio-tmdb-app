package com.harun.tmdbapp.repository

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getPopularMovies(): Flow<PagingData<Movie>>
}