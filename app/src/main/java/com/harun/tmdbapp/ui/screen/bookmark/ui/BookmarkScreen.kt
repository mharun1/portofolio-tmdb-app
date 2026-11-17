package com.harun.tmdbapp.ui.screen.bookmark.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harun.tmdbapp.R
import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.ui.component.state.LoadingScreen
import com.harun.tmdbapp.ui.screen.bookmark.BookmarkUiState
import com.harun.tmdbapp.ui.screen.bookmark.BookmarkViewModel
import com.harun.tmdbapp.ui.screen.bookmark.ui.component.BookmarkItem

@Composable
fun BookmarkRoute(
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel(),
    onItemClick: (Int) -> Unit,
    onExploreClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BookmarkScreen(
        uiState = uiState,
        onItemClick = onItemClick,
        onRemoveBookmark = viewModel::removeBookmark,
        onExploreClick = onExploreClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    uiState: BookmarkUiState,
    onItemClick: (Int) -> Unit,
    onRemoveBookmark: (BookmarkedMovie) -> Unit,
    onExploreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.bookmark_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is BookmarkUiState.Loading -> LoadingScreen(Modifier.fillMaxSize())
                is BookmarkUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.bookmark_empty),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.bookmark_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onExploreClick) {
                            Text(text = stringResource(id = R.string.bookmark_explore_cta))
                        }
                    }
                }
                is BookmarkUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = uiState.bookmarks, key = { it.movieId }) { movie ->
                            BookmarkItem(
                                movie = movie,
                                onClick = { onItemClick(movie.movieId) },
                                onRemove = { onRemoveBookmark(movie) }
                            )
                        }
                    }
                }
            }
        }
    }
}
