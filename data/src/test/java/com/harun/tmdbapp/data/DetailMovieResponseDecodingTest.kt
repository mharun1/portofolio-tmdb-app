package com.harun.tmdbapp.data

import com.harun.tmdbapp.data.remote.response.DetailMovieResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailMovieResponseDecodingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes detail movie response with nested credits, verifying serial names and that crew is ignored`() {
        val body = """
        {
          "id": 550,
          "title": "Fight Club",
          "overview": "A ticking-time-bomb insomniac...",
          "backdrop_path": "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
          "genres": [
            { "id": 18, "name": "Drama" },
            { "id": 53, "name": "Thriller" }
          ],
          "runtime": 139,
          "vote_average": 8.433,
          "release_date": "1999-10-15",
          "spoken_languages": [
            { "english_name": "English", "iso_639_1": "en", "name": "English" }
          ],
          "credits": {
            "cast": [
              {
                "id": 819,
                "name": "Edward Norton",
                "character": "The Narrator",
                "profile_path": "/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg",
                "order": 0
              },
              {
                "id": 287,
                "name": "Brad Pitt",
                "character": "Tyler Durden",
                "profile_path": null,
                "order": 1
              }
            ],
            "crew": [
              {
                "id": 7467,
                "name": "David Fincher",
                "job": "Director",
                "department": "Directing"
              }
            ]
          },
          "status": "Released",
          "tagline": "Mischief. Mayhem. Soap."
        }
        """.trimIndent()

        val result = json.decodeFromString<DetailMovieResponse>(body)

        assertEquals(550, result.id)
        assertEquals("Fight Club", result.title)
        assertEquals("A ticking-time-bomb insomniac...", result.overview)
        assertEquals("/hZkgoQYus5vegHoetLkCJzb17zJ.jpg", result.backdropUrl)
        assertEquals(2, result.genres.size)
        assertEquals("Drama", result.genres[0].name)
        assertEquals(139, result.runtime)
        assertEquals(8.433, result.rating, 0.0)
        assertEquals("1999-10-15", result.releaseDate)

        // Verifies @SerialName("spoken_languages") and @SerialName("english_name")
        assertEquals(1, result.languages.size)
        assertEquals("English", result.languages[0].englishName)

        // Verifies credits and cast @SerialName("profile_path"), proving "crew" is safely ignored
        val credits = result.credits
        requireNotNull(credits)
        assertEquals(2, credits.cast.size)
        assertEquals(819, credits.cast[0].id)
        assertEquals("Edward Norton", credits.cast[0].name)
        assertEquals("The Narrator", credits.cast[0].character)
        assertEquals("/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg", credits.cast[0].picture)

        // Verifies null profile_path decoding
        assertNull(credits.cast[1].picture)
    }
}
