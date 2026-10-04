package com.harun.tmdbapp.data.remote.retrofit.network

import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import retrofit2.http.GET

interface ApiService {
    @GET("movie/popular")
    suspend fun getMovies(): MovieListResponse

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponse

    @GET("configuration")
    suspend fun getImageConfiguration(): ConfigurationResponse
}