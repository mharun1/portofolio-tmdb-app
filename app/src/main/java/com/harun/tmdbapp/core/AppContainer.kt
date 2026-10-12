package com.harun.tmdbapp.core

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.ui.screen.detail.DetailViewModel
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import com.harun.tmdbapp.usecase.GetPopularMoviesUseCase

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val movieRepository: Repository
    val getPopularMoviesUseCase: GetPopularMoviesUseCase
    val getMovieDetailUseCase: GetMovieDetailUseCase
    fun detailViewModelFactory(movieId: Int): ViewModelProvider.Factory
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    override val movieRepository: Repository by lazy {
        MovieRepository(Network.api)
    }
    override val getPopularMoviesUseCase: GetPopularMoviesUseCase by lazy {
        GetPopularMoviesUseCase(movieRepository)
    }
    override val getMovieDetailUseCase: GetMovieDetailUseCase by lazy {
        GetMovieDetailUseCase(movieRepository)
    }

    override fun detailViewModelFactory(movieId: Int): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                DetailViewModel(
                    movieId = movieId,
                    getMovieDetailUseCase = getMovieDetailUseCase
                )
            }
        }
}