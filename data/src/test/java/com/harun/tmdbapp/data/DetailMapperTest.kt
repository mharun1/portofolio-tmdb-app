package com.harun.tmdbapp.data

import com.harun.tmdbapp.data.mapper.toDomain
import com.harun.tmdbapp.data.remote.response.CastDto
import com.harun.tmdbapp.data.remote.response.CreditsListResponse
import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import com.harun.tmdbapp.data.remote.response.GenreDto
import com.harun.tmdbapp.data.remote.response.LanguageRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailMapperTest {

    @Test
    fun `normal movie maps all fields correctly`() {
        val response = DetailMovieResponse(
            id = 101,
            backdropUrl = "/backdrop.jpg",
            title = "Inception",
            overview = "A mind-bending thriller",
            genres = listOf(
                GenreDto(id = 1, name = "Action"),
                GenreDto(id = 2, name = "Sci-Fi")
            ),
            runtime = 148,
            rating = 8.8,
            releaseDate = "2010-07-16",
            language = listOf(
                LanguageRef(englishName = "English"),
                LanguageRef(englishName = "Japanese")
            ),
            credits = CreditsListResponse(
                cast = listOf(
                    CastDto(
                        id = 10,
                        name = "Leonardo DiCaprio",
                        character = "Cobb",
                        picture = "/leo.jpg"
                    )
                )
            )
        )

        val domain = response.toDomain()

        assertEquals(101, domain.id)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", domain.backdropUrl)
        assertEquals("Inception", domain.title)
        assertEquals("A mind-bending thriller", domain.summary)
        assertEquals(listOf("Action", "Sci-Fi"), domain.genre)
        assertEquals(148, domain.duration)
        assertEquals(8.8, domain.rating, 0.0)
        assertEquals("2010-07-16", domain.releaseDate)
        assertEquals(listOf("English", "Japanese"), domain.language)
        assertEquals(1, domain.cast.size)

        val castMember = domain.cast.first()
        assertEquals(10, castMember.id)
        assertEquals("Leonardo DiCaprio", castMember.name)
        assertEquals("Cobb", castMember.character)
        assertEquals("https://image.tmdb.org/t/p/w185/leo.jpg", castMember.picture)
    }

    @Test
    fun `null images map to null urls`() {
        val response = DetailMovieResponse(
            id = 1,
            backdropUrl = null,
            title = "No Image Movie",
            overview = "Overview",
            genres = emptyList(),
            runtime = 100,
            rating = 7.0,
            releaseDate = "2024-01-01",
            language = emptyList(),
            credits = CreditsListResponse(
                cast = listOf(
                    CastDto(
                        id = 2,
                        name = "Actor",
                        character = "Role",
                        picture = null
                    )
                )
            )
        )

        val domain = response.toDomain()

        assertNull(domain.backdropUrl)
        assertNull(domain.cast.first().picture)
    }

    @Test
    fun `null runtime maps to null duration and zero runtime maps to zero`() {
        val responseWithNullRuntime = DetailMovieResponse(
            id = 1,
            backdropUrl = null,
            title = "Null Runtime Movie",
            overview = "Overview",
            genres = emptyList(),
            runtime = null,
            rating = 7.0,
            releaseDate = "2024-01-01",
            language = emptyList(),
            credits = null
        )

        val responseWithZeroRuntime = responseWithNullRuntime.copy(runtime = 0)

        assertNull(responseWithNullRuntime.toDomain().duration)
        assertEquals(0, responseWithZeroRuntime.toDomain().duration)
    }

    @Test
    fun `empty cast when credits is null or cast list is empty`() {
        val responseNullCredits = DetailMovieResponse(
            id = 1,
            backdropUrl = null,
            title = "No Credits Movie",
            overview = "Overview",
            genres = emptyList(),
            runtime = 90,
            rating = 6.5,
            releaseDate = "2024-01-01",
            language = emptyList(),
            credits = null
        )

        val responseEmptyCast = responseNullCredits.copy(
            credits = CreditsListResponse(cast = emptyList())
        )

        assertTrue(responseNullCredits.toDomain().cast.isEmpty())
        assertTrue(responseEmptyCast.toDomain().cast.isEmpty())
    }
}
