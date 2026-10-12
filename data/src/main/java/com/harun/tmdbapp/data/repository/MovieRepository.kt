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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val apiService: ApiService
): Repository {

    private var genreCache: Map<Int, String>? = null

    private suspend fun getGenreMap(): Map<Int, String> {
        return genreCache ?: try {
            apiService.getGenres().genres
                .associate { it.id to it.name }
                .also { genreCache = it }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    override fun getPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { MoviePagingSource(apiService, ::getGenreMap) }
        ).flow
    }

    override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
        return try {
            val movie = apiService.getDetailMovie(id).toDomain()
            Result.success(movie)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
