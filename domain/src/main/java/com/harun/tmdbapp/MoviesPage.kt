package com.harun.tmdbapp

data class MoviesPage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int,
)
