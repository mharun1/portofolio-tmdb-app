package com.harun.tmdbapp.usecase

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertSame
import org.junit.Test

class GetPopularMoviesUseCaseTest {

    @Test
    fun `use case returns what the fake repository provides`() {
        val expectedFlow = flowOf(PagingData.empty<Movie>())
        
        val fakeRepository = object : Repository {
            override fun getPopularMovies(): Flow<PagingData<Movie>> {
                return expectedFlow
            }

            override suspend fun getDetailMovie(id: Int): MovieDetail {
                throw NotImplementedError()
            }
        }

        val useCase = GetPopularMoviesUseCase(fakeRepository)
        val resultFlow = useCase()

        assertSame("The returned flow should be the exact same instance provided by the repository", expectedFlow, resultFlow)
    }
}
