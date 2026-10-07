package com.harun.tmdbapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.ui.component.ErrorScreen
import com.harun.tmdbapp.ui.component.LoadingScreen
import com.harun.tmdbapp.ui.component.MovieItem

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<Movie>,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            count = movies.itemCount,
            // Removed the custom 'key' here to prevent crashes when the API returns duplicate movies across pages.
            // Compose will now safely fall back to using the item's list index as the key.
        ) { index ->
            val movie = movies[index]
            if (movie != null) {
                MovieItem(
                    poster = movie.posterUrl,
                    title = movie.title,
                    rating = movie.rating
                )
            }
        }
        if (movies.loadState.append is LoadState.Loading) {
            item {
                LoadingScreen(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        val appendError = movies.loadState.append as? LoadState.Error
        if (appendError != null) {
            item {
                ErrorScreen(
                    modifier = modifier.fillMaxWidth(),
                    errorMessage = "Couldn't load more movies. Check your connection.",
                    onClick = { movies.retry() }
                )
            }
        }
    }
}
