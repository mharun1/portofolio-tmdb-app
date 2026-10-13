package com.harun.tmdbapp.ui.screen.detail

import com.harun.tmdbapp.model.MovieDetail

sealed interface DetailUiState {
    data object Loading: DetailUiState
    data class Success(val movieDetail: MovieDetail): DetailUiState
    data class Error(val errorMessage: String): DetailUiState
}