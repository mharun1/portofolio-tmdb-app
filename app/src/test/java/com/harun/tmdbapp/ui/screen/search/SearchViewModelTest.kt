package com.harun.tmdbapp.ui.screen.search

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.model.MovieDetail
import com.harun.tmdbapp.repository.Repository
import com.harun.tmdbapp.usecase.SearchMovieUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private class FakeRepository : Repository {
        var lastQuery: String? = null
        var returnMovies: List<Movie> = emptyList()

        override fun searchMovies(query: String): Flow<PagingData<Movie>> {
            lastQuery = query
            return flowOf(PagingData.from(returnMovies))
        }

        override fun getMovies(category: MovieCategory): Flow<PagingData<Movie>> = throw NotImplementedError()
        override suspend fun getDetailMovie(id: Int): Result<MovieDetail> = throw NotImplementedError()
        override suspend fun getNowPlayingMovie(): Result<List<Movie>> = throw NotImplementedError()
    }

    @Test
    fun `initial query is empty`() {
        val fakeRepository = FakeRepository()
        val viewModel = SearchViewModel(SearchMovieUseCase(fakeRepository))

        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onQueryChange updates search query state`() {
        val fakeRepository = FakeRepository()
        val viewModel = SearchViewModel(SearchMovieUseCase(fakeRepository))

        viewModel.onQueryChange("harbor")

        assertEquals("harbor", viewModel.searchQuery.value)
    }

    @Test
    fun `onClearQuery resets search query state to empty`() {
        val fakeRepository = FakeRepository()
        val viewModel = SearchViewModel(SearchMovieUseCase(fakeRepository))

        viewModel.onQueryChange("harbor")
        assertEquals("harbor", viewModel.searchQuery.value)

        viewModel.onClearQuery()
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `query triggers searchMovieUseCase and emits paging items after debounce`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val expectedMovie = Movie(
                id = 1,
                title = "Midnight Harbor",
                posterUrl = "/harbor.jpg",
                genres = listOf("Drama", "Mystery"),
                rating = 7.8
            )
            val fakeRepository = FakeRepository().apply {
                returnMovies = listOf(expectedMovie)
            }
            val viewModel = SearchViewModel(SearchMovieUseCase(fakeRepository))

            viewModel.onQueryChange("harbor")

            advanceTimeBy(350.milliseconds)

            val snapshot = flowOf(viewModel.searchResult.first()).asSnapshot()
            assertEquals("harbor", fakeRepository.lastQuery)
            assertEquals(1, snapshot.size)
            assertEquals("Midnight Harbor", snapshot.first().title)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
