package com.harun.tmdbapp.ui.screen.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.harun.tmdbapp.core.ViewModelFactory
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.ui.component.ErrorScreen
import com.harun.tmdbapp.ui.component.LoadingScreen
import com.harun.tmdbapp.ui.component.MovieItem
import com.harun.tmdbapp.ui.screen.home.HomeViewModel
import com.harun.tmdbapp.ui.screen.home.NowPlayingUiState

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory.Factory),
    onItemClick: (Int) -> Unit,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val nowPlayingUiState by viewModel.nowPlayingUiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(
            nowPlayingUiState = nowPlayingUiState,
            onRetryNowPlaying = viewModel::retry,
            movies = movies,
            selectedCategory = selectedCategory,
            onCategorySelected = viewModel::onCategorySelected,
            modifier = Modifier.padding(innerPadding),
            onItemClick = onItemClick,
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    nowPlayingUiState: NowPlayingUiState,
    onRetryNowPlaying: () -> Unit = {},
    movies: LazyPagingItems<Movie>,
    selectedCategory: MovieCategory = MovieCategory.POPULAR,
    onCategorySelected: (MovieCategory) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (Int) -> Unit,
) {
    var previousCategory by rememberSaveable { mutableStateOf(selectedCategory) }

    LaunchedEffect(selectedCategory) {
        if (previousCategory != selectedCategory) {
            previousCategory = selectedCategory
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // 1. Now Playing Carousel
        item(key = "now_playing_section") {
            NowPlayingSection(
                uiState = nowPlayingUiState,
                onRetry = onRetryNowPlaying,
                onItemClick = onItemClick,
                modifier = Modifier.padding(top = 24.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        // 2. Popular / Top Rated Switch
        item(key = "category_switch") {
            CategorySwitch(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
            )
            Spacer(Modifier.height(4.dp))
        }

        // 3. Paged list and its refresh states (inside the list below the switch)
        when (movies.loadState.refresh) {
            is LoadState.Loading -> {
                item(key = "movies_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingScreen()
                    }
                }
            }
            is LoadState.Error -> {
                item(key = "movies_error") {
                    ErrorScreen(
                        modifier = Modifier.fillMaxWidth(),
                        errorMessage = "Couldn't load movies. Check your connection.",
                        onClick = { movies.retry() },
                    )
                }
            }
            is LoadState.NotLoading -> {
                if (movies.itemCount == 0) {
                    item(key = "movies_empty") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No movies found",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    items(
                        count = movies.itemCount,
                        key = { index -> "${movies[index]?.id ?: "movie"}_$index" },
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
                                errorMessage = "Couldn't load more movies. Check your connection.",
                                onClick = { movies.retry() },
                            )
                        }
                    }
                }
            }
        }
    }
}
