package com.harun.tmdbapp.model

data class MovieDetail(
    val id: Int,
    val backdropUrl: String? = null,
    val title: String,
    val summary: String,
    val genre: List<String>,
    val duration: Int? = 0,
    val rating: Double,
    val releaseDate: String,
    val language: List<String>,
    val cast: List<CastMember>
)
