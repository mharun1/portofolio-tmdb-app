package com.harun.tmdbapp.data.repository

import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.data.remote.response.CastDto
import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.CreditsListResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreDto
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.LanguageRef
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.model.MovieCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class MovieRepositoryTest {

    private class FakeApiService : ApiService {
        var shouldThrowException = false
        var exceptionToThrow: Throwable = IOException("Network error: request failed")
        var popularResponse: MovieListResponse = MovieListResponse(
            page = 1,
            results = emptyList(),
            totalPages = 1
        )
        var topRatedResponse: MovieListResponse = MovieListResponse(
            page = 1,
            results = emptyList(),
            totalPages = 1
        )
        var searchResponse: MovieListResponse = MovieListResponse(
            page = 1,
            results = emptyList(),
            totalPages = 1
        )
        var lastSearchQuery: String? = null
        var nowPlayingResponse: MovieListResponse = MovieListResponse(
            page = 1,
            results = emptyList(),
            totalPages = 1
        )
        var creditsResponse: CreditsListResponse? = CreditsListResponse(
            cast = listOf(
                CastDto(
                    id = 1,
                    name = "Leonardo DiCaprio",
                    character = "Cobb",
                    picture = "/leo.jpg",
                    creditId = "credit_leo_1"
                )
            )
        )

        override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse {
            if (shouldThrowException) {
                throw exceptionToThrow
            }
            return DetailMovieResponse(
                id = id,
                backdropUrl = "/backdrop.jpg",
                title = "Inception",
                overview = "A mind-bending thriller",
                genres = listOf(GenreDto(1, "Action")),
                runtime = 148,
                rating = 8.8,
                releaseDate = "2010-07-16",
                languages = listOf(LanguageRef(englishName = "English")),
                credits = creditsResponse
            )
        }

        override suspend fun getNowPlayingMovie(): MovieListResponse {
            if (shouldThrowException) {
                throw exceptionToThrow
            }
            return nowPlayingResponse
        }

        override suspend fun getPopularMovies(page: Int): MovieListResponse {
            if (shouldThrowException) {
                throw exceptionToThrow
            }
            return popularResponse
        }

        override suspend fun getTopRatedMovies(page: Int): MovieListResponse {
            if (shouldThrowException) {
                throw exceptionToThrow
            }
            return topRatedResponse
        }

        override suspend fun searchMovies(query: String, page: Int): MovieListResponse {
            if (shouldThrowException) {
                throw exceptionToThrow
            }
            lastSearchQuery = query
            return searchResponse
        }

        override suspend fun getGenres(): GenreListResponse = GenreListResponse(emptyList())
        override suspend fun getImageConfiguration(): ConfigurationResponse = throw NotImplementedError()
    }

    @Test
    fun `getDetailMovie success returns mapped movie detail with cast`() = runTest {
        val fakeApi = FakeApiService()
        val repository = MovieRepository(fakeApi)

        val result = repository.getDetailMovie(101)

        assertTrue(result.isSuccess)
        val movie = result.getOrThrow()
        assertEquals(101, movie.id)
        assertEquals("Inception", movie.title)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", movie.backdropUrl)
        assertEquals(1, movie.cast.size)
        assertEquals("Leonardo DiCaprio", movie.cast.first().name)
        assertEquals("credit_leo_1", movie.cast.first().creditId)
    }

    @Test
    fun `getDetailMovie failure returns Result failure when details request fails`() = runTest {
        val fakeApi = FakeApiService().apply { shouldThrowException = true }
        val repository = MovieRepository(fakeApi)

        val result = repository.getDetailMovie(101)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun `getDetailMovie returns movie detail with empty cast when credits are null`() = runTest {
        val fakeApi = FakeApiService().apply { creditsResponse = null }
        val repository = MovieRepository(fakeApi)

        val result = repository.getDetailMovie(101)

        assertTrue(result.isSuccess)
        val movie = result.getOrThrow()
        assertEquals(101, movie.id)
        assertEquals("Inception", movie.title)
        assertTrue(movie.cast.isEmpty())
    }

    @Test(expected = CancellationException::class)
    fun `getDetailMovie does not catch CancellationException`() = runTest {
        val fakeApi = object : ApiService {
            override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse {
                throw CancellationException("Cancelled")
            }
            override suspend fun getNowPlayingMovie(): MovieListResponse = throw NotImplementedError()
            override suspend fun getPopularMovies(page: Int): MovieListResponse = throw NotImplementedError()
            override suspend fun getTopRatedMovies(page: Int): MovieListResponse = throw NotImplementedError()
            override suspend fun searchMovies(query: String, page: Int): MovieListResponse = throw NotImplementedError()
            override suspend fun getGenres(): GenreListResponse = throw NotImplementedError()
            override suspend fun getImageConfiguration(): ConfigurationResponse = throw NotImplementedError()
        }
        val repository = MovieRepository(fakeApi)

        repository.getDetailMovie(101)
    }

    @Test
    fun `getMovies returns paged data for given category`() = runTest {
        val popularDto = MovieDto(
            id = 1,
            title = "Popular Movie",
            poster = "/pop.jpg",
            genreIds = emptyList(),
            rating = 7.5
        )
        val topRatedDto = MovieDto(
            id = 2,
            title = "Top Rated Movie",
            poster = "/top.jpg",
            genreIds = emptyList(),
            rating = 9.0
        )
        val fakeApi = FakeApiService().apply {
            popularResponse = MovieListResponse(
                page = 1,
                results = listOf(popularDto),
                totalPages = 1
            )
            topRatedResponse = MovieListResponse(
                page = 1,
                results = listOf(topRatedDto),
                totalPages = 1
            )
        }
        val repository = MovieRepository(fakeApi)

        val popularMovies = repository.getMovies(MovieCategory.POPULAR).asSnapshot()
        val topRatedMovies = repository.getMovies(MovieCategory.TOP_RATED).asSnapshot()

        assertEquals(1, popularMovies.size)
        assertEquals("Popular Movie", popularMovies.first().title)
        assertEquals(1, topRatedMovies.size)
        assertEquals("Top Rated Movie", topRatedMovies.first().title)
    }

    @Test
    fun `searchMovies returns paged data from search endpoint`() = runTest {
        val searchDto = MovieDto(
            id = 10,
            title = "Midnight Harbor",
            poster = "/harbor.jpg",
            genreIds = emptyList(),
            rating = 8.5
        )
        val fakeApi = FakeApiService().apply {
            searchResponse = MovieListResponse(
                page = 1,
                results = listOf(searchDto),
                totalPages = 1
            )
        }
        val repository = MovieRepository(fakeApi)

        val searchMovies = repository.searchMovies("harbor").asSnapshot()

        assertEquals(1, searchMovies.size)
        assertEquals("Midnight Harbor", searchMovies.first().title)
        assertEquals("harbor", fakeApi.lastSearchQuery)
    }

    @Test
    fun `getNowPlayingMovie returns mapped movies capped at 10 items`() = runTest {
        val fakeApi = FakeApiService().apply {
            nowPlayingResponse = MovieListResponse(
                page = 1,
                results = (1..20).map { index ->
                    MovieDto(
                        id = index,
                        title = "Movie $index",
                        poster = "/poster$index.jpg",
                        genreIds = emptyList(),
                        rating = 7.5
                    )
                },
                totalPages = 1
            )
        }
        val repository = MovieRepository(fakeApi)

        val result = repository.getNowPlayingMovie()

        assertTrue(result.isSuccess)
        val movies = result.getOrThrow()
        assertEquals(10, movies.size)
        assertEquals(1, movies.first().id)
        assertEquals(10, movies.last().id)
        assertEquals("Movie 1", movies.first().title)
    }

    @Test
    fun `getNowPlayingMovie returns failure Result when API call fails`() = runTest {
        val fakeApi = FakeApiService().apply {
            shouldThrowException = true
            exceptionToThrow = IOException("Now playing API failed")
        }
        val repository = MovieRepository(fakeApi)

        val result = repository.getNowPlayingMovie()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test(expected = CancellationException::class)
    fun `getNowPlayingMovie rethrows CancellationException`() = runTest {
        val fakeApi = FakeApiService().apply {
            shouldThrowException = true
            exceptionToThrow = CancellationException("Coroutines job cancelled")
        }
        val repository = MovieRepository(fakeApi)

        repository.getNowPlayingMovie()
    }
}
