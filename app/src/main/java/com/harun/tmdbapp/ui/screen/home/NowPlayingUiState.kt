package com.harun.tmdbapp.ui.screen.home

import com.harun.tmdbapp.model.Movie

sealed interface NowPlayingUiState {
    data object Loading: NowPlayingUiState
    data class Success(val movies: List<Movie>): NowPlayingUiState
    data class Error(val message: String): NowPlayingUiState
}