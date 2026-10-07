package com.harun.tmdbapp.data.mapper.detail

import com.harun.tmdbapp.data.helper.toUrl
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.model.MovieDetail

private const val BACKDROP_SIZE = "w780"

fun DetailMovieResponse.toDomain(): MovieDetail = MovieDetail(
    id = id,
    backdropUrl = backdropUrl.toUrl(BACKDROP_SIZE),
    title = title,
    summary = overview,
    genre = genres.map { it.name },
    duration = runtime,
    rating = rating,
    releaseDate = releaseDate,
    language = language.mapNotNull { it.englishName }
)