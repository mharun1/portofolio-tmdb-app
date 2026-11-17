package com.harun.tmdbapp.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val preferredLanguage: String = "en"
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
