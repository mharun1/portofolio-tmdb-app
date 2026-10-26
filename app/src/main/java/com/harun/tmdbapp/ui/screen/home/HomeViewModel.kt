package com.harun.tmdbapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.usecase.GetMoviesUseCase
import com.harun.tmdbapp.usecase.GetNowPlayingMovieUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getNowPlayingMovieUseCase: GetNowPlayingMovieUseCase
): ViewModel() {

    private val _selectedCategory = MutableStateFlow(MovieCategory.POPULAR)
    val selectedCategory: StateFlow<MovieCategory> = _selectedCategory.asStateFlow()

    val movies: Flow<PagingData<Movie>> = _selectedCategory
        .flatMapLatest { category ->
            getMoviesUseCase(category)
        }
        .cachedIn(viewModelScope)

    fun onCategorySelected(category: MovieCategory) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
        }
    }

    private val _nowPlayingUiState = MutableStateFlow<NowPlayingUiState>(NowPlayingUiState.Loading)
    val nowPlayingUiState: StateFlow<NowPlayingUiState> = _nowPlayingUiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadNowPlayingMovie()
    }

    fun retry() {
        loadNowPlayingMovie()
    }

    fun loadNowPlayingMovie() {
        loadJob?.cancel()
        _nowPlayingUiState.value = NowPlayingUiState.Loading
        loadJob = viewModelScope.launch {
            getNowPlayingMovieUseCase()
                .onSuccess { movie ->
                    _nowPlayingUiState.value = NowPlayingUiState.Success(movie)
                }
                .onFailure {
                    _nowPlayingUiState.value = NowPlayingUiState.Error("Couldn't load current on air movies. Please check your connection.")
                }
        }
    }
}
