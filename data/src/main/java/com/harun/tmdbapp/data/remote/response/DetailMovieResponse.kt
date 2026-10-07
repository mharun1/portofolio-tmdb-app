package com.harun.tmdbapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailMovieResponse(
    val id: Int,
    @SerialName("backdrop_path")
    val backdropUrl: String?,
    val title: String,
    val overview: String,
    val genres: List<GenreDto>,
    val runtime: Int?,
    @SerialName("vote_average")
    val rating: Double,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("spoken_languages")
    val language: List<LanguageRef>,
)

@Serializable
data class LanguageRef(
    @SerialName("english_name")
    val englishName: String?,
)