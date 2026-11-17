package com.harun.tmdbapp.repository

import com.harun.tmdbapp.model.ThemeMode
import com.harun.tmdbapp.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setPreferredLanguage(language: String)
    suspend fun clearCache()
}
