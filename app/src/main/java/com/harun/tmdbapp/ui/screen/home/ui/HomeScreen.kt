package com.harun.tmdbapp.ui.screen.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.harun.tmdbapp.ui.screen.home.HomeViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory.Factory),
    onItemClick: (Int) -> Unit,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(
            movies = movies,
            selectedCategory = selectedCategory,
            onCategorySelected = viewModel::onCategorySelected,
            modifier = Modifier.padding(innerPadding),
            onItemClick = onItemClick
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<Movie>,
    selectedCategory: MovieCategory = MovieCategory.POPULAR,
    onCategorySelected: (MovieCategory) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (Int) -> Unit
) {
    var previousCategory by rememberSaveable { mutableStateOf(selectedCategory) }

    LaunchedEffect(selectedCategory) {
        if (previousCategory != selectedCategory) {
            previousCategory = selectedCategory
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val isPopularSelected = selectedCategory == MovieCategory.POPULAR
            Button(
                onClick = { onCategorySelected(MovieCategory.POPULAR) },
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 4.dp,
                    bottomEnd = 4.dp
                ),
                colors = if (isPopularSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = "Popular",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            val isTopRatedSelected = selectedCategory == MovieCategory.TOP_RATED
            Button(
                onClick = { onCategorySelected(MovieCategory.TOP_RATED) },
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    bottomStart = 4.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                ),
                colors = if (isTopRatedSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = "Top Rated",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        when (movies.loadState.refresh) {
            is LoadState.Error -> {
                ErrorScreen(
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = "Couldn't load movies. Check your connection.",
                    onClick = { movies.retry() }
                )
            }
            is LoadState.Loading -> {
                LoadingScreen(modifier = Modifier.fillMaxSize())
            }
            is LoadState.NotLoading -> {
                if (movies.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No movies found", textAlign = TextAlign.Center)
                    }
                } else {
                    HomeContent(
                        movies = movies,
                        listState = listState,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}
