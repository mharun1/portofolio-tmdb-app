package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetNowPlayingMovieUseCaseTest {

    private class FakeRepository : Repository {
        var callCount = 0
        var resultToReturn: Result<List<Movie>>? = null

        override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> = throw NotImplementedError()
        override fun searchMovies(query: String): Flow<PagingData<Movie>> = throw NotImplementedError()
        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> = throw NotImplementedError()

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> {
            callCount++
            return resultToReturn ?: throw IllegalStateException("resultToReturn not set")
        }
    }

    @Test
    fun `use case passes through repository result on success`() = runTest {
        val fakeRepository = FakeRepository()
        val expectedMovies = listOf(
            Movie(id = 1, title = "Movie 1", posterUrl = null, genres = emptyList(), rating = 8.0),
            Movie(id = 2, title = "Movie 2", posterUrl = null, genres = emptyList(), rating = 7.5),
        )
        fakeRepository.resultToReturn = Result.success(expectedMovies)

        val useCase = GetNowPlayingMovieUseCase(fakeRepository)
        val result = useCase()

        assertEquals(1, fakeRepository.callCount)
        assertTrue(result.isSuccess)
        assertSame(expectedMovies, result.getOrNull())
    }

    @Test
    fun `use case passes through repository result on failure`() = runTest {
        val fakeRepository = FakeRepository()
        val expectedException = RuntimeException("Network failure")
        fakeRepository.resultToReturn = Result.failure(expectedException)

        val useCase = GetNowPlayingMovieUseCase(fakeRepository)
        val result = useCase()

        assertEquals(1, fakeRepository.callCount)
        assertTrue(result.isFailure)
        assertSame(expectedException, result.exceptionOrNull())
    }
}
