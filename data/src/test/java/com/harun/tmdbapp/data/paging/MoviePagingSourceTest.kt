package com.harun.tmdbapp.data.paging

import androidx.paging.PagingSource
import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.MovieCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class MoviePagingSourceTest {

    private class FakeApiService(
        private val totalPages: Int = 2,
        private val shouldThrow: Boolean = false
    ) : ApiService {
        var popularCalled = false
        var topRatedCalled = false
        var lastPageRequested: Int? = null

        override suspend fun getPopularMovies(page: Int): MovieListResponse {
            popularCalled = true
            lastPageRequested = page
            if (shouldThrow) throw IOException("Network error")
            return MovieListResponse(
                page = page,
                results = listOf(
                    MovieDto(id = 1, title = "Popular Movie $page", poster = null, genreIds = listOf(1), rating = 8.0)
                ),
                totalPages = totalPages
            )
        }

        override suspend fun getTopRatedMovies(page: Int): MovieListResponse {
            topRatedCalled = true
            lastPageRequested = page
            if (shouldThrow) throw IOException("Network error")
            return MovieListResponse(
                page = page,
                results = listOf(
                    MovieDto(id = 2, title = "Top Rated Movie $page", poster = null, genreIds = listOf(1), rating = 9.0)
                ),
                totalPages = totalPages
            )
        }

        override suspend fun getNowPlayingMovie(): MovieListResponse =
            throw NotImplementedError()

        override suspend fun searchMovies(query: String, page: Int): MovieListResponse =
            throw NotImplementedError()

        override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse =
            throw NotImplementedError()

        override suspend fun getGenres(): GenreListResponse =
            throw NotImplementedError()

        override suspend fun getImageConfiguration(): ConfigurationResponse =
            throw NotImplementedError()
    }

    @Test
    fun `paging source passes Popular category to the fake API`() = runTest {
        val fakeApi = FakeApiService()
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.POPULAR)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        assertTrue("getPopularMovies should be called", fakeApi.popularCalled)
        assertFalse("getTopRatedMovies should not be called", fakeApi.topRatedCalled)
        assertEquals(1, fakeApi.lastPageRequested)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Popular Movie 1", page.data.first().title)
    }

    @Test
    fun `paging source passes Top Rated category to the fake API`() = runTest {
        val fakeApi = FakeApiService()
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.TOP_RATED)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        assertTrue("getTopRatedMovies should be called", fakeApi.topRatedCalled)
        assertFalse("getPopularMovies should not be called", fakeApi.popularCalled)
        assertEquals(1, fakeApi.lastPageRequested)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Top Rated Movie 1", page.data.first().title)
    }

    @Test
    fun `first page returns the movies, prevKey is null and nextKey is 2`() = runTest {
        val fakeApi = FakeApiService(totalPages = 2)
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.POPULAR)

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
        val fakeApi = FakeApiService(totalPages = 2)
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.POPULAR)

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
        val fakeApi = FakeApiService(totalPages = 1000)
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.POPULAR)

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
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `an IOException from the API gives LoadResult_Error`() = runTest {
        val fakeApi = FakeApiService(shouldThrow = true)
        val pagingSource = MoviePagingSource(apiService = fakeApi, category = MovieCategory.POPULAR)

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
