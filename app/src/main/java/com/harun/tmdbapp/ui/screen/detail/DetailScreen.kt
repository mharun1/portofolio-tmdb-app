package com.harun.tmdbapp.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                Column {
                    Text(text = movie.title)
                    Text(text = movie.summary)
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                    Button(onClick = onBack) {
                        Text("Back to Home")
                    }
                }
            }
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
