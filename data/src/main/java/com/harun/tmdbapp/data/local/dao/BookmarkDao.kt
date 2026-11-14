package com.harun.tmdbapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harun.tmdbapp.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE movieId = :movieId)")
    fun isBookmarked(movieId: Int): Flow<Boolean>

    @Query("SELECT userRating FROM bookmarks WHERE movieId = :movieId")
    fun getUserRating(movieId: Int): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE movieId = :movieId")
    suspend fun delete(movieId: Int)

    @Query("UPDATE bookmarks SET userRating = :rating WHERE movieId = :movieId")
    suspend fun updateUserRating(movieId: Int, rating: Int)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAll()
}
