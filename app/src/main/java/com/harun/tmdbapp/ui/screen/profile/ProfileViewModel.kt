package com.harun.tmdbapp.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harun.tmdbapp.model.ThemeMode
import com.harun.tmdbapp.model.UserPreferences
import com.harun.tmdbapp.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setPreferredLanguage(language: String) {
        viewModelScope.launch {
            preferencesRepository.setPreferredLanguage(language)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            preferencesRepository.clearCache()
            _snackbarMessage.value = "Local cache cleared successfully"
        }
    }

    fun onSnackbarDismissed() {
        _snackbarMessage.value = null
    }
}
