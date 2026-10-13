package com.harun.tmdbapp.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val genres: List<String>,
    val rating: Double
)
