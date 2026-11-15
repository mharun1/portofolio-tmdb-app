package com.harun.tmdbapp.data.local.mapper

import com.harun.tmdbapp.data.local.entity.BookmarkEntity
import com.harun.tmdbapp.model.BookmarkedMovie

fun BookmarkEntity.toDomain(): BookmarkedMovie {
    return BookmarkedMovie(
        movieId = movieId,
        title = title,
        posterUrl = posterUrl,
        genres = if (genres.isBlank()) emptyList() else genres.split(",").map { it.trim() },
        rating = rating,
        userRating = userRating,
        bookmarkedAt = bookmarkedAt
    )
}

fun BookmarkedMovie.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        movieId = movieId,
        title = title,
        posterUrl = posterUrl,
        genres = genres.joinToString(","),
        rating = rating,
        userRating = userRating,
        bookmarkedAt = bookmarkedAt
    )
}
