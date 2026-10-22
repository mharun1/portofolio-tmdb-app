package com.harun.tmdbapp.ui.screen.home

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetMoviesUseCase
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
    fun `The ViewModel starts on Popular`() = runTest {
        val fakeRepository = object : Repository {
            override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> =
                flowOf(PagingData.empty())

            override suspend fun getDetailMovie(id: Int): Result<MovieDetail> =
                throw NotImplementedError()
        }
        val viewModel = HomeViewModel(GetMoviesUseCase(fakeRepository))

        assertEquals(MovieCategory.POPULAR, viewModel.selectedCategory.value)
    }

    @Test
    fun `onCategorySelected updates selectedCategory state`() = runTest {
        val fakeRepository = object : Repository {
            override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> =
                flowOf(PagingData.empty())

            override suspend fun getDetailMovie(id: Int): Result<MovieDetail> =
                throw NotImplementedError()
        }
        val viewModel = HomeViewModel(GetMoviesUseCase(fakeRepository))

        viewModel.onCategorySelected(MovieCategory.TOP_RATED)

        assertEquals(MovieCategory.TOP_RATED, viewModel.selectedCategory.value)
    }

    @Test
    fun `Selecting Top Rated emits that category's data`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val popularMovies = listOf(
                Movie(
                    id = 1,
                    title = "Popular Movie",
                    posterUrl = null,
                    genres = emptyList(),
                    rating = 7.0,
                ),
            )
            val topRatedMovies = listOf(
                Movie(
                    id = 2,
                    title = "Top Rated Movie",
                    posterUrl = null,
                    genres = emptyList(),
                    rating = 9.5,
                ),
            )

            val fakeRepository = object : Repository {
                override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> {
                    return when (category) {
                        MovieCategory.POPULAR -> flowOf(PagingData.from(popularMovies))
                        MovieCategory.TOP_RATED -> flowOf(PagingData.from(topRatedMovies))
                    }
                }

                override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
                    throw NotImplementedError()
                }
            }

            val viewModel = HomeViewModel(GetMoviesUseCase(fakeRepository))

            // Initial emissions reflect Popular
            val initialSnapshot = flowOf(viewModel.movies.first()).asSnapshot()
            assertEquals(popularMovies, initialSnapshot)

            // Selecting Top Rated emits that category's data
            viewModel.onCategorySelected(MovieCategory.TOP_RATED)

            val updatedSnapshot = flowOf(viewModel.movies.first()).asSnapshot()
            assertEquals(topRatedMovies, updatedSnapshot)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
