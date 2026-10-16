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
            languages = listOf(
                LanguageRef(englishName = "English"),
                LanguageRef(englishName = "Japanese")
            ),
            credits = CreditsListResponse(
                cast = listOf(
                    CastDto(
                        id = 10,
                        name = "Leonardo DiCaprio",
                        character = "Cobb",
                        picture = "/leo.jpg",
                        creditId = "credit_10"
                    )
                )
            )
        )

        val domain = response.toDomain()

        assertEquals(101, domain.id)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", domain.backdropUrl)
        assertEquals("Inception", domain.title)
        assertEquals("A mind-bending thriller", domain.summary)
        assertEquals(listOf("Action", "Sci-Fi"), domain.genres)
        assertEquals(148, domain.runtimeMinutes)
        assertEquals(8.8, domain.rating, 0.0)
        assertEquals("2010-07-16", domain.releaseDate)
        assertEquals(listOf("English", "Japanese"), domain.languages)
        assertEquals(1, domain.cast.size)

        val castMember = domain.cast.first()
        assertEquals(10, castMember.id)
        assertEquals("Leonardo DiCaprio", castMember.name)
        assertEquals("Cobb", castMember.character)
        assertEquals("https://image.tmdb.org/t/p/w185/leo.jpg", castMember.picture)
        assertEquals("credit_10", castMember.creditId)
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
            languages = emptyList(),
            credits = CreditsListResponse(
                cast = listOf(
                    CastDto(
                        id = 2,
                        name = "Actor",
                        character = "Role",
                        picture = null,
                        creditId = "credit_2"
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
            languages = emptyList(),
            credits = null
        )

        val responseWithZeroRuntime = responseWithNullRuntime.copy(runtime = 0)

        assertNull(responseWithNullRuntime.toDomain().runtimeMinutes)
        assertEquals(0, responseWithZeroRuntime.toDomain().runtimeMinutes)
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
            languages = emptyList(),
            credits = null
        )

        val responseEmptyCast = responseNullCredits.copy(
            credits = CreditsListResponse(cast = emptyList())
        )

        assertTrue(responseNullCredits.toDomain().cast.isEmpty())
        assertTrue(responseEmptyCast.toDomain().cast.isEmpty())
    }

    @Test
    fun `cast is capped at 10 and preserves billing order when more than 10 are provided`() {
        val fifteenCastMembers = (0 until 15).map { index ->
            CastDto(
                id = index,
                name = "Actor $index",
                character = "Character $index",
                picture = null,
                creditId = "credit_$index"
            )
        }

        val response = DetailMovieResponse(
            id = 1,
            backdropUrl = null,
            title = "Ensemble Movie",
            overview = "Overview",
            genres = emptyList(),
            runtime = 120,
            rating = 8.0,
            releaseDate = "2024-01-01",
            languages = emptyList(),
            credits = CreditsListResponse(cast = fifteenCastMembers)
        )

        val domain = response.toDomain()

        assertEquals(10, domain.cast.size)
        domain.cast.forEachIndexed { index, castMember ->
            assertEquals(index, castMember.id)
            assertEquals("Actor $index", castMember.name)
            assertEquals("Character $index", castMember.character)
            assertEquals("credit_$index", castMember.creditId)
        }
    }

    @Test
    fun `maps multiple cast entries with the same person id but different creditIds`() {
        val response = DetailMovieResponse(
            id = 1,
            backdropUrl = null,
            title = "Dual Role Movie",
            overview = "Overview",
            genres = emptyList(),
            runtime = 120,
            rating = 8.0,
            releaseDate = "2024-01-01",
            languages = emptyList(),
            credits = CreditsListResponse(
                cast = listOf(
                    CastDto(
                        id = 42,
                        name = "Eddie Murphy",
                        character = "Prince Akeem",
                        picture = "/eddie.jpg",
                        creditId = "credit_akeem"
                    ),
                    CastDto(
                        id = 42,
                        name = "Eddie Murphy",
                        character = "Randy Watson",
                        picture = "/eddie.jpg",
                        creditId = "credit_randy"
                    )
                )
            )
        )

        val domain = response.toDomain()

        assertEquals(2, domain.cast.size)
        assertEquals(42, domain.cast[0].id)
        assertEquals("Prince Akeem", domain.cast[0].character)
        assertEquals("credit_akeem", domain.cast[0].creditId)

        assertEquals(42, domain.cast[1].id)
        assertEquals("Randy Watson", domain.cast[1].character)
        assertEquals("credit_randy", domain.cast[1].creditId)
    }
}
