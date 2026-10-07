package com.harun.tmdbapp.data.mapper.list

import com.harun.tmdbapp.data.helper.toUrl
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.model.Movie

private const val POSTER_SIZE = "w500"

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterUrl = poster.toUrl(POSTER_SIZE),
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