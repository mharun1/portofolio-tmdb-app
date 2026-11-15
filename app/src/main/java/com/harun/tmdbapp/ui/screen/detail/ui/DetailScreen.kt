package com.harun.tmdbapp.ui.screen.detail.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harun.tmdbapp.di.ViewModelFactoryProvider
import com.harun.tmdbapp.ui.component.state.ErrorScreen
import com.harun.tmdbapp.ui.component.state.LoadingScreen
import com.harun.tmdbapp.ui.screen.detail.DetailUiState
import com.harun.tmdbapp.ui.screen.detail.DetailViewModel
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme
import dagger.hilt.android.EntryPointAccessors

@Composable
fun DetailRoute(
    modifier: Modifier = Modifier,
    movieId: Int,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ViewModelFactoryProvider::class.java
        ).detailViewModelFactory()
    }
    val viewModel: DetailViewModel = viewModel(
        key = "detail_$movieId",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(movieId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Surface(modifier = modifier.fillMaxSize()) {
        DetailScreen(
            uiState = uiState,
            onRetry = viewModel::retry,
            onToggleBookmark = viewModel::toggleBookmark,
            onRatingChanged = viewModel::setUserRating,
            onBack = onBack,
        )
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    uiState: DetailUiState,
    onRetry: () -> Unit,
    onToggleBookmark: () -> Unit = {},
    onRatingChanged: (Int) -> Unit = {},
    onBack: () -> Unit
) {
    AnimatedContent(
        targetState = uiState,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 350)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 200))
        },
        label = "detail_screen_transition"
    ) { state ->
        when (state) {
            is DetailUiState.Error -> {
                ErrorScreen(
                    modifier = modifier.fillMaxSize(),
                    errorMessage = state.errorMessage,
                    onClick = onRetry,
                )
            }
            DetailUiState.Loading -> {
                LoadingScreen(modifier = modifier.fillMaxSize())
            }
            is DetailUiState.Success -> {
                val movie = state.movieDetail
                DetailContent(
                    movie = movie,
                    isBookmarked = state.isBookmarked,
                    userRating = state.userRating,
                    onToggleBookmark = onToggleBookmark,
                    onRatingChanged = onRatingChanged,
                    onBack = onBack,
                    modifier = modifier.fillMaxSize()
                )
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

