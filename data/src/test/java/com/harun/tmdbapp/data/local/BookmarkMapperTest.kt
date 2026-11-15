package com.harun.tmdbapp.data.local

import com.harun.tmdbapp.data.local.entity.BookmarkEntity
import com.harun.tmdbapp.data.local.mapper.toDomain
import com.harun.tmdbapp.data.local.mapper.toEntity
import com.harun.tmdbapp.model.BookmarkedMovie
import org.junit.Assert.assertEquals
import org.junit.Test

class BookmarkMapperTest {

    @Test
    fun `toDomain maps BookmarkEntity correctly`() {
        val entity = BookmarkEntity(
            movieId = 100,
            title = "Interstellar",
            posterUrl = "/interstellar.jpg",
            genres = "Adventure,Drama,Sci-Fi",
            rating = 8.6,
            userRating = 5,
            bookmarkedAt = 123456789L
        )

        val domain = entity.toDomain()

        assertEquals(100, domain.movieId)
        assertEquals("Interstellar", domain.title)
        assertEquals("/interstellar.jpg", domain.posterUrl)
        assertEquals(listOf("Adventure", "Drama", "Sci-Fi"), domain.genres)
        assertEquals(8.6, domain.rating, 0.01)
        assertEquals(5, domain.userRating)
        assertEquals(123456789L, domain.bookmarkedAt)
    }

    @Test
    fun `toEntity maps BookmarkedMovie correctly`() {
        val domain = BookmarkedMovie(
            movieId = 200,
            title = "Dunkirk",
            posterUrl = null,
            genres = listOf("War", "Action"),
            rating = 7.9,
            userRating = 4,
            bookmarkedAt = 987654321L
        )

        val entity = domain.toEntity()

        assertEquals(200, entity.movieId)
        assertEquals("Dunkirk", entity.title)
        assertEquals(null, entity.posterUrl)
        assertEquals("War,Action", entity.genres)
        assertEquals(7.9, entity.rating, 0.01)
        assertEquals(4, entity.userRating)
        assertEquals(987654321L, entity.bookmarkedAt)
    }

    @Test
    fun `toDomain handles blank genres properly`() {
        val entity = BookmarkEntity(
            movieId = 300,
            title = "Short",
            posterUrl = null,
            genres = "",
            rating = 6.0
        )

        val domain = entity.toDomain()
        assertEquals(emptyList<String>(), domain.genres)
    }
}
