package com.harun.tmdbapp.ui.screen.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.harun.tmdbapp.R
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.ui.component.movie.MovieItem
import com.harun.tmdbapp.ui.component.state.ErrorScreen
import com.harun.tmdbapp.ui.component.state.LoadingScreen

fun LazyListScope.movieListItems(
    movies: LazyPagingItems<Movie>,
    onItemClick: (Int) -> Unit,
) {
    items(
        count = movies.itemCount,
        key = { index -> "${movies.peek(index)?.id ?: "movie"}_$index" },
    ) { index ->
        val movie = movies[index]
        if (movie != null) {
            MovieItem(
                poster = movie.posterUrl,
                title = movie.title,
                rating = movie.rating,
                genres = movie.genres,
                onItemClick = { onItemClick(movie.id) },
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
            )
        }
    }

    if (movies.loadState.append is LoadState.Loading) {
        item(key = "movies_append_loading") {
            LoadingScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }

    val appendError = movies.loadState.append as? LoadState.Error
    if (appendError != null) {
        item(key = "movies_append_error") {
            ErrorScreen(
                modifier = Modifier.fillMaxWidth(),
                errorMessage = stringResource(id = R.string.error_append_movies),
                onClick = { movies.retry() },
            )
        }
    }
}
