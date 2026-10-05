package com.harun.tmdbapp.data.remote

import com.harun.tmdbapp.data.remote.mapper.toDomain
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.MoviesPage
import com.harun.tmdbapp.repository.Repository

class MovieRepository(
    private val apiService: ApiService
): Repository {
    override suspend fun getPopularMovies(): MoviesPage {
        return apiService.getPopularMovies().toDomain()
    }

}