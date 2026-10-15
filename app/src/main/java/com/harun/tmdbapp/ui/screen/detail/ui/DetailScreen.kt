package com.harun.tmdbapp.ui.screen.detail.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harun.tmdbapp.core.ViewModelFactory.detailViewModelFactory
import com.harun.tmdbapp.ui.component.ErrorScreen
import com.harun.tmdbapp.ui.component.LoadingScreen
import com.harun.tmdbapp.ui.screen.detail.DetailUiState
import com.harun.tmdbapp.ui.screen.detail.DetailViewModel
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

@Composable
fun DetailRoute(
    modifier: Modifier = Modifier,
    movieId: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(
        factory = detailViewModelFactory(movieId)
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        DetailScreen(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            onRetry = viewModel::retry,
            onBack = onBack
        )
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    uiState: DetailUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    when (uiState) {
        is DetailUiState.Error -> {
            ErrorScreen(
                modifier = modifier.fillMaxSize(),
                errorMessage = uiState.errorMessage,
                onClick = onRetry,
            )
        }
        DetailUiState.Loading -> {
            LoadingScreen(modifier = modifier.fillMaxSize())
        }
        is DetailUiState.Success -> {
            val movie = uiState.movieDetail
            DetailContent(
                backdropUrl = movie.backdropUrl,
                title = movie.title,
                rating = movie.rating,
                runtime = movie.runtimeMinutes,
                language = movie.languages,
                releaseDate = movie.releaseDate,
                summary = movie.summary,
                onBack = onBack,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PortofolioTMDBAppTheme {
        DetailScreen(
            uiState = DetailUiState.Loading,
            onBack = { },
            onRetry = {}
        )
    }
}
