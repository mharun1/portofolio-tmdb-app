package com.harun.tmdbapp.model

data class BookmarkedMovie(
    val movieId: Int,
    val title: String,
    val posterUrl: String?,
    val genres: List<String>,
    val rating: Double,
    val userRating: Int? = null,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
