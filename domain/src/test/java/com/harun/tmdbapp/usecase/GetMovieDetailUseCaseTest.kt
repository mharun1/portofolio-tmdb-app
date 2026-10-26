package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private class FakeRepository : Repository {
        var requestedId: Int? = null
        var resultToReturn: Result<MovieDetail>? = null

        override fun getMovies(category: com.harun.tmdbapp.model.MovieCategory): Flow<PagingData<Movie>> {
            throw NotImplementedError()
        }

        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
            requestedId = id
            return resultToReturn ?: throw IllegalStateException("resultToReturn not set")
        }

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> {
            throw NotImplementedError()
        }
    }

    @Test
    fun `use case passes movieId to repository and returns repository's result on success`() = runTest {
        val fakeRepository = FakeRepository()
        val expectedMovie = MovieDetail(
            id = 42,
            backdropUrl = "/test.jpg",
            title = "Test Movie",
            summary = "Summary",
            genres = listOf("Action"),
            runtimeMinutes = 120,
            rating = 8.5,
            releaseDate = "2024-01-01",
            languages = listOf("English"),
            cast = emptyList()
        )
        fakeRepository.resultToReturn = Result.success(expectedMovie)

        val useCase = GetMovieDetailUseCase(fakeRepository)
        val result = useCase(42)

        assertEquals(42, fakeRepository.requestedId)
        assertTrue(result.isSuccess)
        assertSame(expectedMovie, result.getOrNull())
    }

    @Test
    fun `use case passes movieId to repository and returns repository's result on failure`() = runTest {
        val fakeRepository = FakeRepository()
        val expectedException = RuntimeException("Network failure")
        fakeRepository.resultToReturn = Result.failure(expectedException)

        val useCase = GetMovieDetailUseCase(fakeRepository)
        val result = useCase(99)

        assertEquals(99, fakeRepository.requestedId)
        assertTrue(result.isFailure)
        assertSame(expectedException, result.exceptionOrNull())
    }
}
