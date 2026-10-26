package com.harun.tmdbapp.ui.screen.home.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.ui.component.state.ErrorScreen
import com.harun.tmdbapp.ui.component.state.LoadingScreen
import com.harun.tmdbapp.ui.screen.home.NowPlayingUiState
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

@Composable
fun NowPlayingSection(
    modifier: Modifier = Modifier,
    uiState: NowPlayingUiState,
    onRetry: () -> Unit = {},
    onItemClick: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))
        when (uiState) {
            is NowPlayingUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }
            is NowPlayingUiState.Error -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    errorMessage = uiState.message,
                    onClick = onRetry
                )
            }
            is NowPlayingUiState.Success -> {
                if (uiState.movies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No movies available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        items(
                            items = uiState.movies,
                            key = { it.id }
                        ) { movie ->
                            NowPlayingItem(
                                posterUrl = movie.posterUrl,
                                title = movie.title,
                                rating = movie.rating,
                                onItemClick = { onItemClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NowPlayingSectionPreview() {
    PortofolioTMDBAppTheme {
        NowPlayingSection(
            uiState = NowPlayingUiState.Success(
                movies = listOf(
                    Movie(
                        id = 1,
                        title = "Spider-Man: No Way Home",
                        posterUrl = null,
                        genres = listOf("Action"),
                        rating = 9.1
                    ),
                    Movie(
                        id = 2,
                        title = "Eternals",
                        posterUrl = null,
                        genres = listOf("Action"),
                        rating = 9.5
                    ),
                    Movie(
                        id = 3,
                        title = "Shang-Chi",
                        posterUrl = null,
                        genres = listOf("Action"),
                        rating = 8.1
                    )
                )
            ),
            onItemClick = {}
        )
    }
}
