package com.harun.tmdbapp.data

import com.harun.tmdbapp.data.remote.response.MovieListResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import kotlinx.serialization.json.Json
import org.junit.Test

class MovieListResponseDecodingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a page with a null poster and a real poster along with unknown keys`() {
        val body = """
        { 
          "page": 1, 
          "results": [
            {
              "id": 1,
              "title": "Movie no poster",
              "poster_path": null,
              "genre_ids": [1],
              "vote_average": 8.6,
              "unknown_key": "some_value"
            },
            {
              "id": 2,
              "title": "Movie with poster",
              "poster_path": "/abc.jpg",
              "genre_ids": [1, 2],
              "vote_average": 7.5
            }
          ], 
          "total_pages": 5,
          "another_unknown_key": true
        }
        """.trimIndent()
        
        val result = json.decodeFromString<MovieListResponse>(body)
        
        assertEquals(1, result.page)
        assertEquals(5, result.totalPages)
        assertEquals(2, result.results.size)
        
        val firstMovie = result.results[0]
        assertEquals(1, firstMovie.id)
        assertEquals("Movie no poster", firstMovie.title)
        assertNull(firstMovie.poster)
        assertEquals(listOf(1), firstMovie.genreIds)
        assertEquals(8.6, firstMovie.rating, 0.0)
        
        val secondMovie = result.results[1]
        assertEquals(2, secondMovie.id)
        assertEquals("Movie with poster", secondMovie.title)
        assertEquals("/abc.jpg", secondMovie.poster)
        assertEquals(listOf(1, 2), secondMovie.genreIds)
        assertEquals(7.5, secondMovie.rating, 0.0)
    }
}