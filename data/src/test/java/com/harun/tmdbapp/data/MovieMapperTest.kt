package com.harun.tmdbapp.data

import com.harun.tmdbapp.data.remote.mapper.toDomain
import com.harun.tmdbapp.data.remote.response.MovieDto
import com.harun.tmdbapp.data.remote.response.MovieListResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {

    @Test
    fun `movie without poster maps to null poster`() {
        val dto = MovieDto(
            id = 1,
            title = "Movie no poster",
            poster = null,
            genreIds = listOf(1),
            rating = 8.6
        )

        val movie = dto.toDomain()

        assertNull(movie.posterUrl)
    }

    @Test
    fun `poster path becomes the full URL`() {
        val dto = MovieDto(
            id = 1,
            title = "Movie with poster",
            poster = "/abc.jpg",
            genreIds = listOf(1),
            rating = 8.6
        )

        val movie = dto.toDomain()

        assertEquals("https://image.tmdb.org/t/p/w500/abc.jpg", movie.posterUrl)
    }
    
    @Test
    fun `MovieListResponse toDomain keeps page, totalPages and the number of movies`() {
        val dto = MovieListResponse(
            page = 2,
            results = listOf(
                MovieDto(id = 1, title = "Movie 1", poster = null, genreIds = listOf(1), rating = 8.6),
                MovieDto(id = 2, title = "Movie 2", poster = null, genreIds = listOf(2), rating = 7.5)
            ),
            totalPages = 10
        )
        
        val moviesPage = dto.toDomain()
        
        assertEquals(2, moviesPage.page)
        assertEquals(10, moviesPage.totalPages)
        assertEquals(2, moviesPage.movies.size)
    }
    
    @Test
    fun `id and genreIds come through the mapper unchanged`() {
        val expectedId = 42
        val expectedGenreIds = listOf(1, 3, 5)
        val dto = MovieDto(
            id = expectedId,
            title = "Movie ID",
            poster = null,
            genreIds = expectedGenreIds,
            rating = 8.6
        )
        
        val movie = dto.toDomain()
        
        assertEquals(expectedId, movie.id)
        assertEquals(expectedGenreIds, movie.genreIds)
    }
}