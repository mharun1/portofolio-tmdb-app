package com.harun.tmdbapp.data.remote.mapper

import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.Movie
import com.harun.tmdbapp.MoviesPage

private const val BASE_URL = "https://image.tmdb.org/t/p/"
private const val POSTER_SIZE = "w500"

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterUrl = poster.toPosterUrl(),
        genreIds = genreIds,
        rating = rating
    )
}

fun MovieListResponse.toDomain(): MoviesPage {
    return MoviesPage(
        movies = results.map { it.toDomain() },
        page = page,
        totalPages = totalPages
    )
}

fun String?.toPosterUrl(): String? =
    this?.let { "$BASE_URL$POSTER_SIZE$it" }