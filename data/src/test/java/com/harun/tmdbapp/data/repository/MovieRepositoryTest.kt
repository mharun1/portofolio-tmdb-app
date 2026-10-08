package com.harun.tmdbapp.data.repository

import com.harun.tmdbapp.data.remote.response.CastDto
import com.harun.tmdbapp.data.remote.response.ConfigurationResponse
import com.harun.tmdbapp.data.remote.response.CreditsListResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreDto
import com.harun.tmdbapp.data.remote.response.GenreListResponse
import com.harun.tmdbapp.data.remote.response.LanguageRef
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class MovieRepositoryTest {

    private class FakeApiService : ApiService {
        var shouldThrowException = false
        var creditsResponse: CreditsListResponse? = CreditsListResponse(
            cast = listOf(
                CastDto(id = 1, name = "Leonardo DiCaprio", character = "Cobb", picture = "/leo.jpg")
            )
        )

        override suspend fun getDetailMovie(id: Int, appendToResponse: String): DetailMovieResponse {
            if (shouldThrowException) {
                throw IOException("Network error: details failed")
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

        override suspend fun getPopularMovies(page: Int): MovieListResponse = throw NotImplementedError()
        override suspend fun getGenres(): GenreListResponse = throw NotImplementedError()
        override suspend fun getImageConfiguration(): ConfigurationResponse = throw NotImplementedError()
    }

    @Test
    fun `getDetailMovie success returns mapped movie detail with cast`() = runTest {
        val fakeApi = FakeApiService()
        val repository = MovieRepository(fakeApi)

        val result = repository.getDetailMovie(101)

        assertEquals(101, result.id)
        assertEquals("Inception", result.title)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", result.backdropUrl)
        assertEquals(1, result.cast.size)
        assertEquals("Leonardo DiCaprio", result.cast.first().name)
    }

    @Test(expected = IOException::class)
    fun `getDetailMovie throws exception when details request fails`() = runTest {
        val fakeApi = FakeApiService().apply { shouldThrowException = true }
        val repository = MovieRepository(fakeApi)

        repository.getDetailMovie(101)
    }

    @Test
    fun `getDetailMovie returns movie detail with empty cast when credits are null`() = runTest {
        val fakeApi = FakeApiService().apply { creditsResponse = null }
        val repository = MovieRepository(fakeApi)

        val result = repository.getDetailMovie(101)

        assertEquals(101, result.id)
        assertEquals("Inception", result.title)
        assertTrue(result.cast.isEmpty())
    }
}
