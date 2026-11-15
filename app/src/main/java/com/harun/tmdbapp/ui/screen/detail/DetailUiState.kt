package com.harun.tmdbapp.ui.screen.detail

import com.harun.tmdbapp.model.MovieDetail

sealed interface DetailUiState {
    data object Loading: DetailUiState
    data class Success(
        val movieDetail: MovieDetail,
        val isBookmarked: Boolean = false,
        val userRating: Int? = null
    ): DetailUiState
    data class Error(val errorMessage: String): DetailUiState
}
