package com.harun.tmdbapp.core

import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import com.harun.tmdbapp.usecase.GetMoviesUseCase
import com.harun.tmdbapp.usecase.GetNowPlayingMovieUseCase

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val movieRepository: Repository
    val getMoviesUseCase: GetMoviesUseCase
    val getMovieDetailUseCase: GetMovieDetailUseCase
    val getNowPlayingMovieUseCase: GetNowPlayingMovieUseCase
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
    override val getNowPlayingMovieUseCase: GetNowPlayingMovieUseCase by lazy {
        GetNowPlayingMovieUseCase(movieRepository)
    }
}
