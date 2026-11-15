package com.harun.tmdbapp.data.repository

import com.harun.tmdbapp.data.local.dao.BookmarkDao
import com.harun.tmdbapp.data.local.entity.BookmarkEntity
import com.harun.tmdbapp.model.BookmarkedMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkRepositoryImplTest {

    private class FakeBookmarkDao : BookmarkDao {
        val table = MutableStateFlow<List<BookmarkEntity>>(emptyList())

        override fun getAllBookmarks(): Flow<List<BookmarkEntity>> = table

        override fun isBookmarked(movieId: Int): Flow<Boolean> =
            table.map { list -> list.any { it.movieId == movieId } }

        override fun getUserRating(movieId: Int): Flow<Int?> =
            table.map { list -> list.find { it.movieId == movieId }?.userRating }

        override suspend fun insert(bookmark: BookmarkEntity) {
            val list = table.value.toMutableList()
            list.removeAll { it.movieId == bookmark.movieId }
            list.add(bookmark)
            table.value = list
        }

        override suspend fun delete(movieId: Int) {
            table.value = table.value.filter { it.movieId != movieId }
        }

        override suspend fun updateUserRating(movieId: Int, rating: Int) {
            val list = table.value.toMutableList()
            val index = list.indexOfFirst { it.movieId == movieId }
            if (index >= 0) {
                list[index] = list[index].copy(userRating = rating)
                table.value = list
            }
        }

        override suspend fun clearAll() {
            table.value = emptyList()
        }
    }

    private val sampleMovie = BookmarkedMovie(
        movieId = 50,
        title = "Tenet",
        posterUrl = "/tenet.jpg",
        genres = listOf("Action", "Sci-Fi"),
        rating = 7.5,
        userRating = null
    )

    @Test
    fun `toggleBookmark inserts movie when not present`() = runTest {
        val dao = FakeBookmarkDao()
        val repo = BookmarkRepositoryImpl(dao)

        assertFalse(repo.isBookmarked(50).first())

        repo.toggleBookmark(sampleMovie)
        assertTrue(repo.isBookmarked(50).first())
        val bookmarks = repo.getBookmarkedMovies().first()
        assertEquals(1, bookmarks.size)
        assertEquals("Tenet", bookmarks.first().title)
    }

    @Test
    fun `toggleBookmark deletes movie when already present`() = runTest {
        val dao = FakeBookmarkDao()
        val repo = BookmarkRepositoryImpl(dao)

        repo.toggleBookmark(sampleMovie)
        assertTrue(repo.isBookmarked(50).first())

        repo.toggleBookmark(sampleMovie)
        assertFalse(repo.isBookmarked(50).first())
        assertEquals(0, repo.getBookmarkedMovies().first().size)
    }

    @Test
    fun `setUserRating updates rating in dao`() = runTest {
        val dao = FakeBookmarkDao()
        val repo = BookmarkRepositoryImpl(dao)

        repo.toggleBookmark(sampleMovie)
        assertEquals(null, repo.getUserRating(50).first())

        repo.setUserRating(50, 4)
        assertEquals(4, repo.getUserRating(50).first())
    }

    @Test
    fun `removeBookmark removes from dao`() = runTest {
        val dao = FakeBookmarkDao()
        val repo = BookmarkRepositoryImpl(dao)

        repo.toggleBookmark(sampleMovie)
        assertEquals(1, repo.getBookmarkedMovies().first().size)

        repo.removeBookmark(50)
        assertEquals(0, repo.getBookmarkedMovies().first().size)
    }
}
