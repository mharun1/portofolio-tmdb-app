package com.harun.tmdbapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.harun.tmdbapp.ui.screen.detail.ui.DetailRoute
import com.harun.tmdbapp.ui.screen.home.HomeRoute

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    HomeRoute(
                        modifier = modifier,
                        onItemClick = { id ->
                            backStack.add(Detail(id))
                        }
                    )
                }
                is Detail -> NavEntry(key) {
                    DetailRoute(
                        modifier = modifier,
                        movieId = key.movieId,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}
