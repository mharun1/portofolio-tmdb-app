package com.harun.tmdbapp.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.harun.tmdbapp.core.ViewModelFactory
import com.harun.tmdbapp.ui.component.ErrorScreen
import com.harun.tmdbapp.ui.component.LoadingScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()

    when (val refresh = movies.loadState.refresh) {
        is LoadState.Error -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = refresh.error.message ?: "Unknown error",
                onClick = { movies.retry() }
            )
        }
        is LoadState.Loading -> {
            LoadingScreen(Modifier.fillMaxSize())
        }
        is LoadState.NotLoading -> {
            if (movies.itemCount == 0) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No movies found", textAlign = TextAlign.Center)
                }
            } else {
                Column(modifier = modifier.padding(24.dp)) {
                    Text(
                        text = "Popular",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))
                    HomeContent(movies = movies)
                }
            }
        }
    }
}
