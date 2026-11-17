package com.harun.tmdbapp.ui.screen.profile

import com.harun.tmdbapp.model.ThemeMode
import com.harun.tmdbapp.model.UserPreferences
import com.harun.tmdbapp.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @org.junit.Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakePreferencesRepository : PreferencesRepository {
        val preferencesFlow = MutableStateFlow(UserPreferences())
        var cacheCleared = false

        override val userPreferences: Flow<UserPreferences> = preferencesFlow

        override suspend fun setThemeMode(mode: ThemeMode) {
            preferencesFlow.value = preferencesFlow.value.copy(themeMode = mode)
        }

        override suspend fun setPreferredLanguage(language: String) {
            preferencesFlow.value = preferencesFlow.value.copy(preferredLanguage = language)
        }

        override suspend fun clearCache() {
            cacheCleared = true
        }
    }

    @Test
    fun `initial userPreferences matches repository`() = runTest(testDispatcher) {
        val repo = FakePreferencesRepository()
        val viewModel = ProfileViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.userPreferences.collect()
        }

        advanceUntilIdle()
        assertEquals(ThemeMode.SYSTEM, viewModel.userPreferences.value.themeMode)
        assertEquals("en", viewModel.userPreferences.value.preferredLanguage)
    }

    @Test
    fun `setThemeMode updates theme in repository and state`() = runTest(testDispatcher) {
        val repo = FakePreferencesRepository()
        val viewModel = ProfileViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.userPreferences.collect()
        }

        advanceUntilIdle()
        viewModel.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, viewModel.userPreferences.value.themeMode)
    }

    @Test
    fun `clearCache triggers repository and emits snackbar message`() = runTest(testDispatcher) {
        val repo = FakePreferencesRepository()
        val viewModel = ProfileViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.userPreferences.collect()
        }

        advanceUntilIdle()
        assertNull(viewModel.snackbarMessage.value)

        viewModel.clearCache()
        advanceUntilIdle()

        assertEquals(true, repo.cacheCleared)
        assertNotNull(viewModel.snackbarMessage.value)

        viewModel.onSnackbarDismissed()
        assertNull(viewModel.snackbarMessage.value)
    }
}
