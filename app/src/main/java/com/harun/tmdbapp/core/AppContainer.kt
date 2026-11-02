package com.harun.tmdbapp.core

import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import com.harun.tmdbapp.usecase.GetMoviesUseCase
import com.harun.tmdbapp.usecase.GetNowPlayingMoviesUseCase
import com.harun.tmdbapp.usecase.SearchMovieUseCase

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val movieRepository: Repository
    val getMoviesUseCase: GetMoviesUseCase
    val getMovieDetailUseCase: GetMovieDetailUseCase
    val getNowPlayingMovieUseCase: GetNowPlayingMoviesUseCase
    val searchMovieUseCase: SearchMovieUseCase
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
    override val getMoviesUseCase: GetMoviesUseCase by lazy {
        GetMoviesUseCase(movieRepository)
    }
    override val getMovieDetailUseCase: GetMovieDetailUseCase by lazy {
        GetMovieDetailUseCase(movieRepository)
    }
    override val getNowPlayingMovieUseCase: GetNowPlayingMoviesUseCase by lazy {
        GetNowPlayingMoviesUseCase(movieRepository)
    }
    override val searchMovieUseCase: SearchMovieUseCase by lazy {
        SearchMovieUseCase(movieRepository)
    }
}
