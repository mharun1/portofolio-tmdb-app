package com.harun.tmdbapp.ui.screen.home

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetMoviesUseCase
import com.harun.tmdbapp.usecase.GetNowPlayingMovieUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private class FakeRepository : Repository {
        var nowPlayingResult: Result<List<Movie>> = Result.success(emptyList())

        override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> =
            flowOf(PagingData.empty())

        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> =
            throw NotImplementedError()

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> = nowPlayingResult
    }

    private val sampleNowPlayingMovies = listOf(
        Movie(
            id = 1,
            title = "Spider-Man",
            posterUrl = "/spidey.jpg",
            genres = emptyList(),
            rating = 9.0
        ),
        Movie(
            id = 2,
            title = "Eternals",
            posterUrl = "/eternals.jpg",
            genres = emptyList(),
            rating = 8.5
        )
    )

    @Test
    fun `The ViewModel starts on Popular`() = runTest {
        val fakeRepository = FakeRepository()
        val viewModel = HomeViewModel(
            getMoviesUseCase = GetMoviesUseCase(fakeRepository),
            getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
        )

        assertEquals(MovieCategory.POPULAR, viewModel.selectedCategory.value)
    }

    @Test
    fun `onCategorySelected updates selectedCategory state`() = runTest {
        val fakeRepository = FakeRepository()
        val viewModel = HomeViewModel(
            getMoviesUseCase = GetMoviesUseCase(fakeRepository),
            getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
        )

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

                override suspend fun getNowPlayingMovie(): Result<List<Movie>> =
                    Result.success(emptyList())
            }

            val viewModel = HomeViewModel(
                getMoviesUseCase = GetMoviesUseCase(fakeRepository),
                getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
            )

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

    @Test
    fun `creation emits Loading then Success when getNowPlayingMovie succeeds`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeRepository = FakeRepository().apply {
                nowPlayingResult = Result.success(sampleNowPlayingMovies)
            }
            val viewModel = HomeViewModel(
                getMoviesUseCase = GetMoviesUseCase(fakeRepository),
                getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
            )

            assertEquals(NowPlayingUiState.Loading, viewModel.nowPlayingUiState.value)

            advanceUntilIdle()

            assertEquals(NowPlayingUiState.Success(sampleNowPlayingMovies), viewModel.nowPlayingUiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `creation emits Loading then Error when getNowPlayingMovie fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeRepository = FakeRepository().apply {
                nowPlayingResult = Result.failure(RuntimeException("Network error"))
            }
            val viewModel = HomeViewModel(
                getMoviesUseCase = GetMoviesUseCase(fakeRepository),
                getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
            )

            assertEquals(NowPlayingUiState.Loading, viewModel.nowPlayingUiState.value)

            advanceUntilIdle()

            assertTrue(viewModel.nowPlayingUiState.value is NowPlayingUiState.Error)
            assertEquals(
                "Couldn't load current on air movies. Please check your connection.",
                (viewModel.nowPlayingUiState.value as NowPlayingUiState.Error).message
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `retry transitions to Loading then Success`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeRepository = FakeRepository().apply {
                nowPlayingResult = Result.failure(RuntimeException("Network error"))
            }
            val viewModel = HomeViewModel(
                getMoviesUseCase = GetMoviesUseCase(fakeRepository),
                getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
            )
            advanceUntilIdle()
            assertTrue(viewModel.nowPlayingUiState.value is NowPlayingUiState.Error)

            // Setup success for retry
            fakeRepository.nowPlayingResult = Result.success(sampleNowPlayingMovies)

            viewModel.retry()

            assertEquals(NowPlayingUiState.Loading, viewModel.nowPlayingUiState.value)

            advanceUntilIdle()

            assertEquals(NowPlayingUiState.Success(sampleNowPlayingMovies), viewModel.nowPlayingUiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `switching list category does not touch carousel state`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fakeRepository = FakeRepository().apply {
                nowPlayingResult = Result.success(sampleNowPlayingMovies)
            }
            val viewModel = HomeViewModel(
                getMoviesUseCase = GetMoviesUseCase(fakeRepository),
                getNowPlayingMovieUseCase = GetNowPlayingMovieUseCase(fakeRepository)
            )
            advanceUntilIdle()

            val initialCarouselState = viewModel.nowPlayingUiState.value
            assertEquals(NowPlayingUiState.Success(sampleNowPlayingMovies), initialCarouselState)

            // Switch category
            viewModel.onCategorySelected(MovieCategory.TOP_RATED)
            advanceUntilIdle()

            // Selected category changed, but carousel state remains untouched
            assertEquals(MovieCategory.TOP_RATED, viewModel.selectedCategory.value)
            assertEquals(initialCarouselState, viewModel.nowPlayingUiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
