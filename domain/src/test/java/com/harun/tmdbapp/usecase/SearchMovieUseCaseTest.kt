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

class SearchMovieUseCaseTest {

    private class FakeRepository : Repository {
        var requestedQuery: String? = null
        val flowToReturn = flowOf(PagingData.empty<Movie>())

        override fun searchMovies(query: String): Flow<PagingData<Movie>> {
            requestedQuery = query
            return flowToReturn
        }

        override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> = throw NotImplementedError()
        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> = throw NotImplementedError()
        override suspend fun getNowPlayingMovie(): Result<List<Movie>> = throw NotImplementedError()
    }

    @Test
    fun `use case passes query to repository and returns repository flow`() {
        val fakeRepository = FakeRepository()
        val useCase = SearchMovieUseCase(fakeRepository)

        val resultFlow = useCase("harbor")

        assertEquals("harbor", fakeRepository.requestedQuery)
        assertSame("The returned flow should be the exact same instance provided by the repository", fakeRepository.flowToReturn, resultFlow)
    }
}
