package com.harun.tmdbapp.ui.screen.home

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Test
    fun `ViewModel's movies emits what the fake repository provides`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val testMovie = Movie(
                id = 1,
                title = "Test Movie",
                posterUrl = null,
                genreIds = emptyList(),
                rating = 9.0
            )
            val expectedPagingData = PagingData.from(listOf(testMovie))

            val fakeRepository = object : Repository {
                override fun getPopularMovies(): Flow<PagingData<Movie>> {
                    return flowOf(expectedPagingData)
                }

                override suspend fun getDetailMovie(id: Int): MovieDetail {
                    throw NotImplementedError()
                }
            }
            val useCase = GetPopularMoviesUseCase(fakeRepository)

            val viewModel = HomeViewModel(useCase)

            val emittedPagingData = viewModel.movies.first()
            val snapshot = flowOf(emittedPagingData).asSnapshot()

            assertEquals(1, snapshot.size)
            assertEquals(testMovie, snapshot[0])
            
        } finally {
            Dispatchers.resetMain()
        }
    }
}
