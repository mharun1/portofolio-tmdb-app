package com.harun.tmdbapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val movieId: Int,
    val title: String,
    val posterUrl: String?,
    val genres: String,
    val rating: Double,
    val userRating: Int? = null,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
