package com.harun.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harun.tmdbapp.model.ThemeMode
import com.harun.tmdbapp.navigation.Navigation
import com.harun.tmdbapp.ui.screen.profile.ProfileViewModel
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by profileViewModel.userPreferences.collectAsStateWithLifecycle()
            val isDark = when (preferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            PortofolioTMDBAppTheme(darkTheme = isDark) {
                Navigation()
            }
        }
    }
}
