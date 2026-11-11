package com.harun.tmdbapp.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.harun.tmdbapp.ui.screen.detail.ui.DetailRoute
import com.harun.tmdbapp.ui.screen.home.ui.HomeRoute
import com.harun.tmdbapp.ui.screen.search.ui.SearchRoute

@Composable
fun Navigation(modifier: Modifier = Modifier) {

    val navState = rememberNavigationState()
    val isBottomBarVisible = navState.currentKey is TopLevelDestination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    TOP_LEVEL_DESTINATION.forEach { destination ->
                        val isSelected = destination == navState.topLevelDestination
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.defaultIcon,
                                    contentDescription = destination.label,
                                )
                            },
                            label = { Text(destination.label) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            onClick = {
                                if (isSelected) {
                                    while (navState.currentStack.size > 1) {
                                        navState.currentStack.removeLastOrNull()
                                    }
                                } else {
                                    navState.navigate(destination)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = navState.currentStack,
            onBack = { navState.goBack() },
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
                            onItemClick = { id -> navState.navigate(Detail(id)) },
                            onSearchClick = { navState.navigate(Search) }
                        )
                    }
                    is Bookmark -> NavEntry(key) {
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Bookmark Screen")
                        }
                    }
                    is Profile -> NavEntry(key) {
                        Box(
                            modifier = modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Profile Screen")
                        }
                    }
                    is Detail -> NavEntry(key) {
                        DetailRoute(
                            modifier = modifier,
                            movieId = key.movieId,
                            onBack = { navState.goBack() },
                        )
                    }
                    is Search -> NavEntry(key) {
                        SearchRoute(
                            modifier = modifier,
                            onItemClick = { id -> navState.navigate(Detail(id)) },
                            onBack = { navState.goBack() }
                        )
                    }
                    else -> throw IllegalArgumentException("Unknown key: $key")
                }
            }
        )
    }
}
