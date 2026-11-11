package com.harun.tmdbapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.harun.tmdbapp.data.mapper.toDomain
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.Movie
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

private const val MAX_TMDB_PAGES = 500

/**
 * Dedicated PagingSource for searching movies.
 *
 * A separate PagingSource was chosen over extending [MoviePagingSource] because:
 * 1. Single Responsibility Principle: [MoviePagingSource] handles fixed feed categories
 *    (Popular and Top Rated), while search handles dynamic user text queries.
 * 2. Search-specific behavior: Search requires validating and short-circuiting blank queries
 *    without network calls, and delegates to a distinct API endpoint (`search/movie`).
 * 3. Independent evolution: Feed paging and search paging can change their configurations,
 *    caching, or parameters independently without risking regression in the other.
 */
class SearchMoviePagingSource(
    private val apiService: ApiService,
    private val query: String,
    private val getGenres: suspend () -> Map<Int, String> = { emptyMap() }
): PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        val page = params.key ?: 1

        return try {
            val genreMap = getGenres()
            val response = apiService.searchMovies(query, page).toDomain(genreMap)
            val lastPage = minOf(response.totalPages, MAX_TMDB_PAGES)

            LoadResult.Page(
                data = response.movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.totalPages == 0 || page >= lastPage) null else page + 1
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