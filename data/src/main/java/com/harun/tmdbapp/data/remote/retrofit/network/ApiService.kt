package com.harun.tmdbapp.data.remote.retrofit.network

import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponse

    @GET("configuration")
    suspend fun getImageConfiguration(): ConfigurationResponse

    @GET("movie/{id}")
    suspend fun getDetailMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): DetailMovieResponse
}
