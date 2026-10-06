package com.harun.tmdbapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras

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
    // We will register ViewModels here as we create them.
    // Example usage in the future:
    // val Factory = viewModelFactory {
    //     initializer {
    //         val appContainer = tmdbApplication().container
    //         MyViewModel(appContainer.movieRepository)
    //     }
    // }
}