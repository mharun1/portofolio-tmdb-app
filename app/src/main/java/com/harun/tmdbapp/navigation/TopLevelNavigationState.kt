package com.harun.tmdbapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

@Composable
fun rememberNavigationState(
    startRoute: TopLevelDestination = Home,
    topLevelRoutes: List<TopLevelDestination> = TOP_LEVEL_DESTINATION
): NavigationState {
    var currentTopLevelIndex by rememberSaveable {
        val initialIndex = topLevelRoutes.indexOf(startRoute).coerceAtLeast(0)
        mutableIntStateOf(initialIndex)
    }
    val backStack = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }
    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            currentTopLevel = { topLevelRoutes.getOrElse(currentTopLevelIndex) { startRoute } },
            onTopLevelChange = { newDestination ->
                val newIndex = topLevelRoutes.indexOf(newDestination)
                if (newIndex >= 0) {
                    currentTopLevelIndex = newIndex
                }
            },
            backStack = backStack
        )
    }
}

class NavigationState(
    val startRoute: TopLevelDestination,
    private val currentTopLevel: () -> TopLevelDestination,
    private val onTopLevelChange: (TopLevelDestination) -> Unit,
    val backStack: Map<TopLevelDestination, NavBackStack<NavKey>>
) {
    var topLevelDestination: TopLevelDestination
        get() = currentTopLevel()
        set(value) = onTopLevelChange(value)

    val currentStack: NavBackStack<NavKey>
        get() = backStack[topLevelDestination] ?: error("No stack for $topLevelDestination")

    val currentKey: NavKey
        get() = currentStack.last()

    fun navigate(key: NavKey) {
        if (key is TopLevelDestination && backStack.containsKey(key)) {
            topLevelDestination = key
        } else {
            currentStack.add(key)
        }
    }

    fun goBack(): Boolean {
        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
            return true
        }
        if (topLevelDestination != startRoute) {
            topLevelDestination = startRoute
            return true
        }
        return false
    }
}
