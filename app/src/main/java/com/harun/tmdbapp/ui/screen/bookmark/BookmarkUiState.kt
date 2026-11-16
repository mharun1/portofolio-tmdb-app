package com.harun.tmdbapp.ui.screen.bookmark

import com.harun.tmdbapp.model.BookmarkedMovie

sealed interface BookmarkUiState {
    data object Loading : BookmarkUiState
    data class Success(val bookmarks: List<BookmarkedMovie>) : BookmarkUiState
    data class Empty(val message: String = "No bookmarks yet") : BookmarkUiState
}
