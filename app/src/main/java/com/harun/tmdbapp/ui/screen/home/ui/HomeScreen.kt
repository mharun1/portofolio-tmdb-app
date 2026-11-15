package com.harun.tmdbapp.ui.screen.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.harun.tmdbapp.R
import com.harun.tmdbapp.model.Movie
import com.harun.tmdbapp.model.MovieCategory
import com.harun.tmdbapp.ui.component.about.AboutBottomSheet
import com.harun.tmdbapp.ui.component.about.TmdbAttributionFooter
import com.harun.tmdbapp.ui.component.state.ErrorScreen
import com.harun.tmdbapp.ui.component.state.LoadingScreen
import com.harun.tmdbapp.ui.screen.home.HomeViewModel
import com.harun.tmdbapp.ui.screen.home.NowPlayingUiState
import com.harun.tmdbapp.ui.screen.home.ui.component.CategorySwitch
import com.harun.tmdbapp.ui.screen.home.ui.component.NowPlayingSection
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit = {},
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val nowPlayingUiState by viewModel.nowPlayingUiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    HomeScreen(
        nowPlayingUiState = nowPlayingUiState,
        onRetryNowPlaying = viewModel::retry,
        movies = movies,
        selectedCategory = selectedCategory,
        onCategorySelected = viewModel::onCategorySelected,
        modifier = modifier.fillMaxSize(),
        onItemClick = onItemClick,
        onSearchClick = onSearchClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    onSearchClick: () -> Unit = {},
) {
    var previousCategory by rememberSaveable { mutableStateOf(selectedCategory) }
    var showAboutSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedCategory) {
        if (previousCategory != selectedCategory) {
            previousCategory = selectedCategory
            listState.scrollToItem(0)
        }
    }

    if (showAboutSheet) {
        AboutBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showAboutSheet = false
                    }
                }
            }
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
    ) {
        // App Header with Title and TMDB Attribution / About icon button
        item(key = "home_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.app_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showAboutSheet = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(id = R.string.about_button_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Search Bar (navigates to Search Screen)
        item(key = "search_bar") {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search_icon_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.search_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 1. Now Playing Carousel
        item(key = "now_playing_section") {
            NowPlayingSection(
                uiState = nowPlayingUiState,
                onRetry = onRetryNowPlaying,
                onItemClick = onItemClick,
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
                        errorMessage = stringResource(id = R.string.error_movies),
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
                                text = stringResource(id = R.string.empty_movies),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    movieListItems(
                        movies = movies,
                        onItemClick = onItemClick,
                    )

                    // TMDB Attribution Footer at end of movies list
                    item(key = "tmdb_attribution_footer") {
                        TmdbAttributionFooter()
                    }
                }
            }
        }
    }
}
