package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetMoviesUseCaseTest {

    private class FakeRepository : Repository {
        var requestedCategory: MovieCategory? = null
        val flowToReturn = flowOf(PagingData.empty<Movie>())

        override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> {
            requestedCategory = category
            return flowToReturn
        }

        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
            throw NotImplementedError()
        }

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> {
            throw NotImplementedError()
        }
    }

    @Test
    fun `use case passes popular category to repository and returns flow`() {
        val fakeRepository = FakeRepository()
        val useCase = GetMoviesUseCase(fakeRepository)

        val resultFlow = useCase(MovieCategory.POPULAR)

        assertEquals(MovieCategory.POPULAR, fakeRepository.requestedCategory)
        assertSame("The returned flow should be the exact same instance provided by the repository", fakeRepository.flowToReturn, resultFlow)
    }

    @Test
    fun `use case passes top rated category to repository and returns flow`() {
        val fakeRepository = FakeRepository()
        val useCase = GetMoviesUseCase(fakeRepository)

        val resultFlow = useCase(MovieCategory.TOP_RATED)

        assertEquals(MovieCategory.TOP_RATED, fakeRepository.requestedCategory)
        assertSame("The returned flow should be the exact same instance provided by the repository", fakeRepository.flowToReturn, resultFlow)
    }
}
