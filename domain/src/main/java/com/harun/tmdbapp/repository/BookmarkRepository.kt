package com.harun.tmdbapp.repository

import com.harun.tmdbapp.model.BookmarkedMovie
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarkedMovies(): Flow<List<BookmarkedMovie>>
    fun isBookmarked(movieId: Int): Flow<Boolean>
    fun getUserRating(movieId: Int): Flow<Int?>
    suspend fun toggleBookmark(movie: BookmarkedMovie)
    suspend fun setUserRating(movieId: Int, rating: Int)
    suspend fun removeBookmark(movieId: Int)
}
