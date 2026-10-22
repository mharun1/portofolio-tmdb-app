package com.harun.tmdbapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.usecase.GetMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
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
}
