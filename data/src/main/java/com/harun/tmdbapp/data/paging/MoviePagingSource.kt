package com.harun.tmdbapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.harun.tmdbapp.data.mapper.list.toDomain
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.Movie
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

private const val MAX_TMDB_PAGES = 500

class MoviePagingSource(
    private val apiService: ApiService
): PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getPopularMovies(page).toDomain()
            val lastPage = minOf(response.totalPages, MAX_TMDB_PAGES)

            LoadResult.Page(
                data = response.movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= lastPage) null else page + 1,
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: SerializationException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}