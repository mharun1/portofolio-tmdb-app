package com.harun.tmdbapp.core

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harun.tmdbapp.ui.screen.home.HomeViewModel

/**
 * Extension function to easily access the AppContainer from the CreationExtras.
 */
fun CreationExtras.tmdbApplication(): TmdbApplication =
    (this[APPLICATION_KEY] as TmdbApplication)

/**
 * Factory for all ViewModels in the app.
 * Using a common factory avoids repeating the boilerplate to retrieve the AppContainer in every ViewModel.
 */
object ViewModelFactory {
     val Factory = viewModelFactory {
        initializer {
            val appContainer = tmdbApplication().container
            HomeViewModel(appContainer.getPopularMoviesUseCase)
        }
    }
}