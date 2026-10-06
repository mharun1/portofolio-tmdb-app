package com.harun.tmdbapp.ui.screen.home

import androidx.paging.PagingData
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
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ViewModel's movies emits what the fake repository provides`() = runTest {
        // Prepare mock movie
        val expectedPagingData = PagingData.empty<Movie>()

        val fakeRepository = object : Repository {
            override fun getPopularMovies(): Flow<PagingData<Movie>> {
                return flowOf(expectedPagingData)
            }
        }
        val useCase = GetPopularMoviesUseCase(fakeRepository)

        val viewModel = HomeViewModel(useCase)

        val emitted = viewModel.movies.first()
        
        assertNotNull("The ViewModel should emit PagingData from the repository", emitted)
    }
}