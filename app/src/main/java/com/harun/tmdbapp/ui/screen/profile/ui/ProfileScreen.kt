package com.harun.tmdbapp.ui.screen.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harun.tmdbapp.R
import com.harun.tmdbapp.model.ThemeMode
import com.harun.tmdbapp.model.UserPreferences
import com.harun.tmdbapp.ui.component.about.TmdbAttributionFooter
import com.harun.tmdbapp.ui.screen.profile.ProfileViewModel
import com.harun.tmdbapp.ui.screen.profile.ui.component.StorageSection
import com.harun.tmdbapp.ui.screen.profile.ui.component.ThemeSection

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onSnackbarDismissed()
        }
    }

    ProfileScreen(
        preferences = preferences,
        snackbarHostState = snackbarHostState,
        onThemeModeSelected = viewModel::setThemeMode,
        onClearCache = viewModel::clearCache,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    preferences: UserPreferences,
    snackbarHostState: SnackbarHostState,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 72.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            ThemeSection(
                currentTheme = preferences.themeMode,
                onThemeSelected = onThemeModeSelected
            )
            Spacer(Modifier.height(24.dp))
            StorageSection(
                onClearCache = onClearCache
            )
            Spacer(Modifier.height(32.dp))
            TmdbAttributionFooter(modifier = Modifier.padding(0.dp))
            Spacer(Modifier.height(32.dp))
        }
    }
}
