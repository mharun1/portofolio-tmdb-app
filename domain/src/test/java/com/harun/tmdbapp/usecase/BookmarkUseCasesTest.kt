package com.harun.tmdbapp.usecase

import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkUseCasesTest {

    private class FakeBookmarkRepository : BookmarkRepository {
        val bookmarks = MutableStateFlow<List<BookmarkedMovie>>(emptyList())

        override fun getBookmarkedMovies(): Flow<List<BookmarkedMovie>> = bookmarks

        override fun isBookmarked(movieId: Int): Flow<Boolean> =
            bookmarks.map { list -> list.any { it.movieId == movieId } }

        override fun getUserRating(movieId: Int): Flow<Int?> =
            bookmarks.map { list -> list.find { it.movieId == movieId }?.userRating }

        override suspend fun toggleBookmark(movie: BookmarkedMovie) {
            val current = bookmarks.value.toMutableList()
            val existing = current.indexOfFirst { it.movieId == movie.movieId }
            if (existing >= 0) {
                current.removeAt(existing)
            } else {
                current.add(movie)
            }
            bookmarks.value = current
        }

        override suspend fun setUserRating(movieId: Int, rating: Int) {
            val current = bookmarks.value.toMutableList()
            val index = current.indexOfFirst { it.movieId == movieId }
            if (index >= 0) {
                current[index] = current[index].copy(userRating = rating)
                bookmarks.value = current
            }
        }

        override suspend fun removeBookmark(movieId: Int) {
            bookmarks.value = bookmarks.value.filter { it.movieId != movieId }
        }
    }

    private val sampleMovie = BookmarkedMovie(
        movieId = 42,
        title = "Inception",
        posterUrl = "/poster.jpg",
        genres = listOf("Action", "Sci-Fi"),
        rating = 8.8,
        userRating = null
    )

    @Test
    fun `toggleBookmark adds movie when not present and removes when present`() = runTest {
        val repo = FakeBookmarkRepository()
        val toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        val isBookmarkedUseCase = IsBookmarkedUseCase(repo)

        assertFalse(isBookmarkedUseCase(42).first())

        toggleBookmarkUseCase(sampleMovie)
        assertTrue(isBookmarkedUseCase(42).first())

        toggleBookmarkUseCase(sampleMovie)
        assertFalse(isBookmarkedUseCase(42).first())
    }

    @Test
    fun `getBookmarks returns list of added bookmarks`() = runTest {
        val repo = FakeBookmarkRepository()
        val toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        val getBookmarksUseCase = GetBookmarksUseCase(repo)

        assertEquals(0, getBookmarksUseCase().first().size)

        toggleBookmarkUseCase(sampleMovie)
        val list = getBookmarksUseCase().first()
        assertEquals(1, list.size)
        assertEquals("Inception", list.first().title)
    }

    @Test
    fun `setUserRating updates movie rating`() = runTest {
        val repo = FakeBookmarkRepository()
        val toggleBookmarkUseCase = ToggleBookmarkUseCase(repo)
        val setUserRatingUseCase = SetUserRatingUseCase(repo)
        val getUserRatingUseCase = GetUserRatingUseCase(repo)

        toggleBookmarkUseCase(sampleMovie)
        assertEquals(null, getUserRatingUseCase(42).first())

        setUserRatingUseCase(42, 5)
        assertEquals(5, getUserRatingUseCase(42).first())
    }
}
