package com.harun.tmdbapp.data.paging

import androidx.paging.PagingSource
import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class MoviePagingSourceTest {

    class FakeApiService : ApiService {
        var shouldThrowIOException = false
        var totalPages = 2
        
        override suspend fun getPopularMovies(page: Int): MovieListResponse {
            if (shouldThrowIOException) {
                throw IOException("Network error")
            }
            return MovieListResponse(
                page = page,
                results = listOf(
                    MovieDto(id = 1, title = "Movie $page", poster = null, genreIds = listOf(1), rating = 8.0)
                ),
                totalPages = totalPages
            )
        }
        
        override suspend fun getGenres(): GenreListResponse {
            throw NotImplementedError()
        }
        
        override suspend fun getImageConfiguration(): ConfigurationResponse {
            throw NotImplementedError()
        }

        override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse {
            throw NotImplementedError()
        }
    }

    @Test
    fun `first page returns the movies, prevKey is null and nextKey is 2`() = runTest {
        val api = FakeApiService()
        val pagingSource = MoviePagingSource(api)
        
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
        assertEquals(1, page.data.size)
    }

    @Test
    fun `last page has nextKey == null`() = runTest {
        val api = FakeApiService()
        api.totalPages = 2
        val pagingSource = MoviePagingSource(api)
        
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.prevKey)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `page 500 stops pagination even if totalPages is larger`() = runTest {
        val api = FakeApiService()
        // Simulate a huge amount of pages (TMDB often returns e.g. 1000+)
        api.totalPages = 1000
        val pagingSource = MoviePagingSource(api)
        
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 500,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(499, page.prevKey)
        // Ensure we don't ask for page 501, since the TMDB API blocks requests > 500
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `an IOException from the API gives LoadResult_Error`() = runTest {
        val api = FakeApiService()
        api.shouldThrowIOException = true
        val pagingSource = MoviePagingSource(api)
        
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
        
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is IOException)
    }
}
