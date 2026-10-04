package com.harun.tmdbapp

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val genreIds: List<Int>,
    val rating: Double
)
