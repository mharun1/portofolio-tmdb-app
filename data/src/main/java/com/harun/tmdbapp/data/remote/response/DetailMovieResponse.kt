package com.harun.tmdbapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailMovieResponse(
    val id: Int,
    @SerialName("backdrop_path")
    val backdropUrl: String? = null,
    val title: String,
    val overview: String,
    val genres: List<GenreDto> = emptyList(),
    val runtime: Int? = null,
    @SerialName("vote_average")
    val rating: Double,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("spoken_languages")
    val languages: List<LanguageRef> = emptyList(),
    val credits: CreditsListResponse? = null
)

@Serializable
data class LanguageRef(
    @SerialName("english_name")
    val englishName: String? = null,
)
