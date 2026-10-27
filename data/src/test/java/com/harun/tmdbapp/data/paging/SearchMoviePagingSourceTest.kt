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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class SearchMoviePagingSourceTest {

    private class FakeApiService(
        private val totalPages: Int = 2,
        private val shouldThrow: Boolean = false
    ) : ApiService {
        var searchCallCount = 0
        var searchCalled = false
        var lastQueryRequested: String? = null
        var lastPageRequested: Int? = null

        override suspend fun searchMovies(query: String, page: Int): MovieListResponse {
            searchCallCount++
            searchCalled = true
            lastQueryRequested = query
            lastPageRequested = page
            if (shouldThrow) throw IOException("Network error")
            return MovieListResponse(
                page = page,
                results = listOf(
                    MovieDto(id = 1, title = "Search Movie $page", poster = null, genreIds = listOf(1), rating = 8.0)
                ),
                totalPages = totalPages
            )
        }

        override suspend fun getPopularMovies(page: Int): MovieListResponse = throw NotImplementedError()
        override suspend fun getTopRatedMovies(page: Int): MovieListResponse = throw NotImplementedError()
        override suspend fun getNowPlayingMovie(): MovieListResponse = throw NotImplementedError()
        override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse = throw NotImplementedError()
        override suspend fun getGenres(): GenreListResponse = throw NotImplementedError()
        override suspend fun getImageConfiguration(): ConfigurationResponse = throw NotImplementedError()
    }

    @Test
    fun `the paging source passes the query to the fake API`() = runTest {
        val fakeApi = FakeApiService()
        val pagingSource = SearchMoviePagingSource(apiService = fakeApi, query = "harbor")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        assertTrue(fakeApi.searchCalled)
        assertEquals("harbor", fakeApi.lastQueryRequested)
        assertEquals(1, fakeApi.lastPageRequested)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Search Movie 1", page.data.first().title)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `blank query makes no API call (the fake counts calls)`() = runTest {
        val fakeApi = FakeApiService()
        val pagingSource = SearchMoviePagingSource(apiService = fakeApi, query = "   ")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(0, fakeApi.searchCallCount)
        assertFalse("API should not be called when query is blank", fakeApi.searchCalled)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun `query with no results and totalPages 0 has nextKey null`() = runTest {
        val fakeApi = FakeApiService(totalPages = 0)
        val pagingSource = SearchMoviePagingSource(apiService = fakeApi, query = "nonexistentmovie")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertNull(page.prevKey)
        assertNull("nextKey must be null when totalPages == 0", page.nextKey)
    }

    @Test
    fun `last page has nextKey null`() = runTest {
        val fakeApi = FakeApiService(totalPages = 2)
        val pagingSource = SearchMoviePagingSource(apiService = fakeApi, query = "harbor")

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
        assertNull(page.nextKey)
    }

    @Test
    fun `network error returns LoadResult Error`() = runTest {
        val fakeApi = FakeApiService(shouldThrow = true)
        val pagingSource = SearchMoviePagingSource(apiService = fakeApi, query = "harbor")

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
