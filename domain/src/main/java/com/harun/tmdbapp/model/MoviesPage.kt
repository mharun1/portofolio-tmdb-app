package com.harun.tmdbapp.model

data class MoviesPage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int,
)
