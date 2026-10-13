package com.harun.tmdbapp.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val movieId: Int,
    private val getMovieDetailUseCase: GetMovieDetailUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadMovieDetail()
    }

    fun retry() {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        loadJob?.cancel()
        _uiState.value = DetailUiState.Loading
        loadJob = viewModelScope.launch {
            getMovieDetailUseCase(movieId)
                .onSuccess { movie ->
                    _uiState.value = DetailUiState.Success(movie)
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error("Couldn't load movie detail. Check your connection.")
                }
        }
    }
}
