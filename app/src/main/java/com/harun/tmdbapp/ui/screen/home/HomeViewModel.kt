package com.harun.tmdbapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.flow.Flow

class HomeViewModel(
    getPopularMoviesUseCase: GetPopularMoviesUseCase
): ViewModel() {
    val movies: Flow<PagingData<Movie>> = getPopularMoviesUseCase().cachedIn(viewModelScope)
}