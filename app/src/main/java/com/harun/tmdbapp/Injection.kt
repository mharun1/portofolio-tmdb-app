package com.harun.tmdbapp

import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.repository.Repository

object Injection {
    val movieRepository: Repository by lazy { MovieRepository(Network.api) }
}