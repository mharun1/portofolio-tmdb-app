package com.harun.tmdbapp.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
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

            // Floating pill overlay — only on top-level screens
            if (isBottomBarVisible) {
                Surface(
                    shape = RoundedCornerShape(100),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 12.dp,
                    tonalElevation = 12.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(240.dp)
                    ) {
                        TOP_LEVEL_DESTINATION.forEach { destination ->
                            val isSelected = destination == navState.topLevelDestination
                            NavBarItem(
                                defaultIcon = destination.defaultIcon,
                                selectedIcon = destination.selectedIcon,
                                label = destination.label,
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        while (navState.currentStack.size > 1) {
                                            navState.currentStack.removeLastOrNull()
                                        }
                                    } else {
                                        navState.navigate(destination)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
