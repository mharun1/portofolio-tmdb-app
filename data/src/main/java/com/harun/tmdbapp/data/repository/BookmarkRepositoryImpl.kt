package com.harun.tmdbapp.data.repository

import com.harun.tmdbapp.data.local.dao.BookmarkDao
import com.harun.tmdbapp.data.local.mapper.toDomain
import com.harun.tmdbapp.data.local.mapper.toEntity
import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getBookmarkedMovies(): Flow<List<BookmarkedMovie>> {
        return bookmarkDao.getAllBookmarks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun isBookmarked(movieId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(movieId)
    }

    override fun getUserRating(movieId: Int): Flow<Int?> {
        return bookmarkDao.getUserRating(movieId)
    }

    override suspend fun toggleBookmark(movie: BookmarkedMovie) {
        val currentlyBookmarked = bookmarkDao.isBookmarked(movie.movieId).first()
        if (currentlyBookmarked) {
            bookmarkDao.delete(movie.movieId)
        } else {
            bookmarkDao.insert(movie.toEntity())
        }
    }

    override suspend fun setUserRating(movieId: Int, rating: Int) {
        val currentlyBookmarked = bookmarkDao.isBookmarked(movieId).first()
        if (currentlyBookmarked) {
            bookmarkDao.updateUserRating(movieId, rating)
        }
    }

    override suspend fun removeBookmark(movieId: Int) {
        bookmarkDao.delete(movieId)
    }
}
