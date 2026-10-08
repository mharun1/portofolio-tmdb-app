package com.harun.tmdbapp.data.mapper

import com.harun.tmdbapp.data.helper.toUrl
import com.harun.tmdbapp.data.remote.response.CastDto
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.model.CastMember
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail

private const val POSTER_SIZE = "w500"
private const val BACKDROP_SIZE = "w780"
private const val PROFILE_SIZE = "w185"

// TMDB credits are ordered by billing. We cap at 10 to keep the detail screen's horizontal
// cast row fast and focused on the principal cast, preventing excessive image requests for minor roles.
private const val MAX_CAST_COUNT = 10

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

fun DetailMovieResponse.toDomain(): MovieDetail = MovieDetail(
    id = id,
    backdropUrl = backdropUrl.toUrl(BACKDROP_SIZE),
    title = title,
    summary = overview,
    genres = genres.map { it.name },
    runtimeMinutes = runtime,
    rating = rating,
    releaseDate = releaseDate,
    languages = languages.mapNotNull { it.englishName },
    cast = credits?.cast?.take(MAX_CAST_COUNT)?.map { it.toDomain() } ?: emptyList()
)

fun CastDto.toDomain(): CastMember = CastMember(
    id = id,
    name = name,
    picture = picture.toUrl(PROFILE_SIZE),
    character = character
)
