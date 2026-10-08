package com.harun.tmdbapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.harun.tmdbapp.data.mapper.toDomain
import com.harun.tmdbapp.data.paging.MoviePagingSource
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val apiService: ApiService
): Repository {
    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { MoviePagingSource(apiService) }
        ).flow
    }

    override suspend fun getDetailMovie(id: Int): MovieDetail {
        return apiService.getDetailMovie(id).toDomain()
    }
}
