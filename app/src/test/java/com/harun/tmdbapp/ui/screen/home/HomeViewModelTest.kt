package com.harun.tmdbapp.ui.screen.home

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.model.Movie
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
        // Use the same scheduler for the Main dispatcher to prevent deadlocks
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            // Prepare mock movie
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
            }
            val useCase = GetPopularMoviesUseCase(fakeRepository)

            val viewModel = HomeViewModel(useCase)

            // Collect the first PagingData and wrap it in a fresh Flow to safely use asSnapshot
            // bypassing the SharedFlow hanging issue from cachedIn
            val emittedPagingData = viewModel.movies.first()
            val snapshot = flowOf(emittedPagingData).asSnapshot()
            
            // Assert that the list equals the one we put in
            assertEquals(1, snapshot.size)
            assertEquals(testMovie, snapshot[0])
            
        } finally {
            Dispatchers.resetMain()
        }
    }
}