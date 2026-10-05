package com.harun.tmdbapp.repository

import com.harun.tmdbapp.model.MoviesPage

interface Repository {
    suspend fun getPopularMovies(): MoviesPage
}