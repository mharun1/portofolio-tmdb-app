package com.harun.tmdbapp.data.mapper

import com.harun.tmdbapp.model.Movie

data class MoviesPage(
    val movies: List<Movie>,
    val page: Int,
    val totalPages: Int,
)
