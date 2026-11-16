package com.harun.tmdbapp.ui.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.usecase.GetBookmarksUseCase
import com.harun.tmdbapp.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    getBookmarksUseCase: GetBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase
) : ViewModel() {

    val uiState: StateFlow<BookmarkUiState> = getBookmarksUseCase()
        .map { bookmarks ->
            if (bookmarks.isEmpty()) {
                BookmarkUiState.Empty()
            } else {
                BookmarkUiState.Success(bookmarks)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BookmarkUiState.Loading
        )

    fun removeBookmark(movie: BookmarkedMovie) {
        viewModelScope.launch {
            toggleBookmarkUseCase(movie)
        }
    }
}
