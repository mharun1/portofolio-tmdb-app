package com.harun.tmdbapp.ui.screen.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.ui.component.movie.MovieItem
import com.harun.tmdbapp.ui.component.state.ErrorScreen
import com.harun.tmdbapp.ui.component.state.LoadingScreen

@Composable
fun SearchResults(
    query: String,
    movies: LazyPagingItems<Movie>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (query.isBlank()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Type a title to search for movies",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    when (movies.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        }
        is LoadState.Error -> {
            ErrorScreen(
                modifier = modifier.fillMaxSize(),
                errorMessage = "Couldn't load search results. Check your connection.",
                onClick = { movies.retry() }
            )
        }
        is LoadState.NotLoading -> {
            if (movies.itemCount == 0) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No movies found for \"$query\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = movies.itemCount,
                        key = { index -> "${movies[index]?.id ?: "movie"}_$index" }
                    ) { index ->
                        val movie = movies[index]
                        if (movie != null) {
                            MovieItem(
                                poster = movie.posterUrl,
                                title = movie.title,
                                rating = movie.rating,
                                genres = movie.genres,
                                onItemClick = { onItemClick(movie.id) }
                            )
                        }
                    }

                    if (movies.loadState.append is LoadState.Loading) {
                        item(key = "append_loading") {
                            LoadingScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    val appendError = movies.loadState.append as? LoadState.Error
                    if (appendError != null) {
                        item(key = "append_error") {
                            ErrorScreen(
                                modifier = Modifier.fillMaxWidth(),
                                errorMessage = "Couldn't load more movies. Check your connection.",
                                onClick = { movies.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}
