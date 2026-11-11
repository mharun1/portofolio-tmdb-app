package com.harun.tmdbapp.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.harun.tmdbapp.ui.screen.detail.ui.DetailRoute
import com.harun.tmdbapp.ui.screen.home.ui.HomeRoute
import com.harun.tmdbapp.ui.screen.search.ui.SearchRoute

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
        transitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> (fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 250)
            )) togetherWith (slideOutHorizontally(
                targetOffsetX = { fullWidth -> -(fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 200)
            ))
        },
        popTransitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> -(fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 250)
            )) togetherWith (slideOutHorizontally(
                targetOffsetX = { fullWidth -> (fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 200)
            ))
        },
        predictivePopTransitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { fullWidth -> -(fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 250)
            )) togetherWith (slideOutHorizontally(
                targetOffsetX = { fullWidth -> (fullWidth * 0.25f).toInt() },
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 200)
            ))
        },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    HomeRoute(
                        modifier = modifier,
                        onItemClick = { id ->
                            backStack.add(Detail(id))
                        },
                        onSearchClick = {
                            backStack.add(Search)
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
                is Search -> NavEntry(key) {
                    SearchRoute(
                        modifier = modifier,
                        onItemClick = { id ->
                            backStack.add(Detail(id))
                        },
                        onBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}
