package com.harun.tmdbapp.ui.screen.detail

import androidx.paging.PagingData
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private class FakeRepository : Repository {
        var resultToReturn: Result<MovieDetail>? = null
        var requestedId: Int? = null

        override fun getMovies(category: com.harun.tmdbapp.model.MovieCategory): Flow<PagingData<Movie>> = throw NotImplementedError()
        override fun searchMovies(query: String): Flow<PagingData<Movie>> = throw NotImplementedError()

        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
            requestedId = id
            return resultToReturn ?: throw IllegalStateException("resultToReturn not set")
        }

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> = throw NotImplementedError()
    }

    private class SuspendingFakeRepository : Repository {
        val deferredList = mutableListOf<CompletableDeferred<Result<MovieDetail>>>()

        override fun getMovies(category: com.harun.tmdbapp.model.MovieCategory): Flow<PagingData<Movie>> = throw NotImplementedError()
        override fun searchMovies(query: String): Flow<PagingData<Movie>> = throw NotImplementedError()

        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> {
            val deferred = CompletableDeferred<Result<MovieDetail>>()
            deferredList.add(deferred)
            return deferred.await()
        }

        override suspend fun getNowPlayingMovie(): Result<List<Movie>> = throw NotImplementedError()
    }

    private val sampleMovieDetail = MovieDetail(
        id = 101,
        backdropUrl = "/backdrop.jpg",
        title = "Inception",
        summary = "A mind-bending thriller",
        genres = listOf("Action", "Sci-Fi"),
        runtimeMinutes = 148,
        rating = 8.8,
        releaseDate = "2010-07-16",
        languages = listOf("English"),
        cast = emptyList()
    )

    @Test
    fun `creation emits Loading then Success when use case succeeds`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeRepository().apply {
                resultToReturn = Result.success(sampleMovieDetail)
            }
            val useCase = GetMovieDetailUseCase(repository)

            val viewModel = DetailViewModel(movieId = 101, getMovieDetailUseCase = useCase)

            assertEquals(DetailUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()

            assertEquals(101, repository.requestedId)
            assertEquals(DetailUiState.Success(sampleMovieDetail), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `creation emits Loading then Error when use case fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeRepository().apply {
                resultToReturn = Result.failure(RuntimeException("Network error"))
            }
            val useCase = GetMovieDetailUseCase(repository)

            val viewModel = DetailViewModel(movieId = 101, getMovieDetailUseCase = useCase)

            assertEquals(DetailUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()

            assertEquals(101, repository.requestedId)
            assertTrue(viewModel.uiState.value is DetailUiState.Error)
            assertEquals(
                "Couldn't load movie detail. Check your connection.",
                (viewModel.uiState.value as DetailUiState.Error).errorMessage
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `retry after error transitions from Loading to Success`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeRepository().apply {
                resultToReturn = Result.failure(RuntimeException("Network error"))
            }
            val useCase = GetMovieDetailUseCase(repository)

            val viewModel = DetailViewModel(movieId = 101, getMovieDetailUseCase = useCase)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is DetailUiState.Error)

            // Setup repository to succeed on retry
            repository.resultToReturn = Result.success(sampleMovieDetail)

            viewModel.retry()

            // Verify immediately back in Loading state
            assertEquals(DetailUiState.Loading, viewModel.uiState.value)

            advanceUntilIdle()

            // Verify state is now Success
            assertEquals(DetailUiState.Success(sampleMovieDetail), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `rapid retry calls cancel previous in-flight load and reflect only the latest result`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = SuspendingFakeRepository()
            val useCase = GetMovieDetailUseCase(repository)

            val viewModel = DetailViewModel(movieId = 101, getMovieDetailUseCase = useCase)
            advanceUntilIdle()
            assertEquals(1, repository.deferredList.size)
            repository.deferredList[0].complete(Result.failure(RuntimeException("Initial failure")))
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value is DetailUiState.Error)

            val movie1 = sampleMovieDetail.copy(title = "First Retry Result")
            val movie2 = sampleMovieDetail.copy(title = "Second Retry Result")

            // First retry call
            viewModel.retry()
            advanceUntilIdle()
            assertEquals(2, repository.deferredList.size)

            // Second retry call immediately while first is pending
            viewModel.retry()
            advanceUntilIdle()
            assertEquals(3, repository.deferredList.size)

            // Complete the second (latest) retry request first
            repository.deferredList[2].complete(Result.success(movie2))
            advanceUntilIdle()
            assertEquals(DetailUiState.Success(movie2), viewModel.uiState.value)

            // Now complete the older first retry request later
            repository.deferredList[1].complete(Result.success(movie1))
            advanceUntilIdle()

            // State must still reflect only the latest result (movie2)
            assertEquals(DetailUiState.Success(movie2), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
