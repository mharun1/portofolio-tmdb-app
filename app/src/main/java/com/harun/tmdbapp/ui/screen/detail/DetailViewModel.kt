package com.harun.tmdbapp.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harun.tmdbapp.model.BookmarkedMovie
import com.harun.tmdbapp.repository.BookmarkRepository
import com.harun.tmdbapp.usecase.GetMovieDetailUseCase
import com.harun.tmdbapp.usecase.GetUserRatingUseCase
import com.harun.tmdbapp.usecase.IsBookmarkedUseCase
import com.harun.tmdbapp.usecase.SetUserRatingUseCase
import com.harun.tmdbapp.usecase.ToggleBookmarkUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

private object StubBookmarkRepository : BookmarkRepository {
    override fun getBookmarkedMovies(): Flow<List<BookmarkedMovie>> = emptyFlow()
    override fun isBookmarked(movieId: Int): Flow<Boolean> = emptyFlow()
    override fun getUserRating(movieId: Int): Flow<Int?> = emptyFlow()
    override suspend fun toggleBookmark(movie: BookmarkedMovie) {}
    override suspend fun setUserRating(movieId: Int, rating: Int) {}
    override suspend fun removeBookmark(movieId: Int) {}
}

class DetailViewModel @AssistedInject constructor(
    @Assisted private val movieId: Int,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val setUserRatingUseCase: SetUserRatingUseCase,
    private val getUserRatingUseCase: GetUserRatingUseCase
): ViewModel() {

    constructor(
        movieId: Int,
        getMovieDetailUseCase: GetMovieDetailUseCase
    ) : this(
        movieId = movieId,
        getMovieDetailUseCase = getMovieDetailUseCase,
        isBookmarkedUseCase = IsBookmarkedUseCase(StubBookmarkRepository),
        toggleBookmarkUseCase = ToggleBookmarkUseCase(StubBookmarkRepository),
        setUserRatingUseCase = SetUserRatingUseCase(StubBookmarkRepository),
        getUserRatingUseCase = GetUserRatingUseCase(StubBookmarkRepository)
    )

    @AssistedFactory
    interface Factory {
        fun create(movieId: Int): DetailViewModel
    }

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var bookmarkJob: Job? = null

    init {
        loadMovieDetail()
        observeBookmarkAndRating()
    }

    fun retry() {
        loadMovieDetail()
    }

    fun toggleBookmark() {
        val currentState = _uiState.value as? DetailUiState.Success ?: return
        val movie = currentState.movieDetail
        viewModelScope.launch {
            toggleBookmarkUseCase(
                BookmarkedMovie(
                    movieId = movie.id,
                    title = movie.title,
                    posterUrl = movie.backdropUrl,
                    genres = movie.genres,
                    rating = movie.rating,
                    userRating = currentState.userRating
                )
            )
        }
    }

    fun setUserRating(rating: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value as? DetailUiState.Success
            if (currentState?.isBookmarked == false) {
                val movie = currentState.movieDetail
                toggleBookmarkUseCase(
                    BookmarkedMovie(
                        movieId = movie.id,
                        title = movie.title,
                        posterUrl = movie.backdropUrl,
                        genres = movie.genres,
                        rating = movie.rating,
                        userRating = rating
                    )
                )
            } else {
                setUserRatingUseCase(movieId, rating)
            }
        }
    }

    private fun loadMovieDetail() {
        loadJob?.cancel()
        _uiState.value = DetailUiState.Loading
        loadJob = viewModelScope.launch {
            getMovieDetailUseCase(movieId)
                .onSuccess { movie ->
                    val currentState = _uiState.value
                    val isBookmarked = (currentState as? DetailUiState.Success)?.isBookmarked ?: false
                    val userRating = (currentState as? DetailUiState.Success)?.userRating
                    _uiState.value = DetailUiState.Success(
                        movieDetail = movie,
                        isBookmarked = isBookmarked,
                        userRating = userRating
                    )
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error("Couldn't load movie detail. Check your connection.")
                }
        }
    }

    private fun observeBookmarkAndRating() {
        bookmarkJob?.cancel()
        bookmarkJob = viewModelScope.launch {
            combine(
                isBookmarkedUseCase(movieId),
                getUserRatingUseCase(movieId)
            ) { isBookmarked, rating ->
                isBookmarked to rating
            }.collect { (isBookmarked, rating) ->
                val current = _uiState.value
                if (current is DetailUiState.Success) {
                    _uiState.value = current.copy(
                        isBookmarked = isBookmarked,
                        userRating = rating
                    )
                }
            }
        }
    }
}

