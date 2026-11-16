package com.harun.tmdbapp.ui.screen.bookmark

import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import com.harun.tmdbapp.usecase.GetBookmarksUseCase
import com.harun.tmdbapp.usecase.ToggleBookmarkUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
class BookmarkViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @org.junit.Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeBookmarkRepository : BookmarkRepository {
        val bookmarksFlow = MutableStateFlow<List<BookmarkedMovie>>(emptyList())

        override fun getBookmarkedMovies(): Flow<List<BookmarkedMovie>> = bookmarksFlow
        override fun isBookmarked(movieId: Int): Flow<Boolean> = throw NotImplementedError()
        override fun getUserRating(movieId: Int): Flow<Int?> = throw NotImplementedError()

        override suspend fun toggleBookmark(movie: BookmarkedMovie) {
            val list = bookmarksFlow.value.toMutableList()
            if (list.any { it.movieId == movie.movieId }) {
                list.removeAll { it.movieId == movie.movieId }
            } else {
                list.add(movie)
            }
            bookmarksFlow.value = list
        }

        override suspend fun setUserRating(movieId: Int, rating: Int) = throw NotImplementedError()
        override suspend fun removeBookmark(movieId: Int) {
            bookmarksFlow.value = bookmarksFlow.value.filter { it.movieId != movieId }
        }
    }

    @Test
    fun `initial state is Empty when repository has no bookmarks`() = runTest(testDispatcher) {
        val repo = FakeBookmarkRepository()
        val viewModel = BookmarkViewModel(
            getBookmarksUseCase = GetBookmarksUseCase(repo),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is BookmarkUiState.Empty)
    }

    @Test
    fun `emits Success when bookmarks are present`() = runTest(testDispatcher) {
        val repo = FakeBookmarkRepository().apply {
            bookmarksFlow.value = listOf(
                BookmarkedMovie(1, "Movie 1", null, emptyList(), 8.0)
            )
        }
        val viewModel = BookmarkViewModel(
            getBookmarksUseCase = GetBookmarksUseCase(repo),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertTrue(state is BookmarkUiState.Success)
        assertEquals(1, (state as BookmarkUiState.Success).bookmarks.size)
    }

    @Test
    fun `removeBookmark removes item and updates state to Empty`() = runTest(testDispatcher) {
        val movie = BookmarkedMovie(1, "Movie 1", null, emptyList(), 8.0)
        val repo = FakeBookmarkRepository().apply {
            bookmarksFlow.value = listOf(movie)
        }
        val viewModel = BookmarkViewModel(
            getBookmarksUseCase = GetBookmarksUseCase(repo),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is BookmarkUiState.Success)

        viewModel.removeBookmark(movie)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is BookmarkUiState.Empty)
    }
}

