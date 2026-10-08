package com.harun.tmdbapp.model

data class MovieDetail(
    val id: Int,
    val backdropUrl: String?,
    val title: String,
    val summary: String,
    val genres: List<String>,
    val runtimeMinutes: Int?,
    val rating: Double,
    val releaseDate: String,
    val languages: List<String>,
    val cast: List<CastMember>
)
