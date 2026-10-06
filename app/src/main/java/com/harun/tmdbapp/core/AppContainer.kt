package com.harun.tmdbapp.core

import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetPopularMoviesUseCase

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val movieRepository: Repository
    val getPopularMoviesUseCase: GetPopularMoviesUseCase
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
}