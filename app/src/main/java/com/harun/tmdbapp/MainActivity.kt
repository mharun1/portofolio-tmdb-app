package com.harun.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.harun.tmdbapp.navigation.Detail
import com.harun.tmdbapp.navigation.Home
import com.harun.tmdbapp.ui.screen.detail.DetailRoute
import com.harun.tmdbapp.ui.screen.home.HomeRoute
import com.harun.tmdbapp.ui.theme.PortofolioTMDBAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortofolioTMDBAppTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Home)

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
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
                            modifier = Modifier.padding(innerPadding),
                            onItemClick = { id ->
                                backStack.add(Detail(id))
                            }
                        )
                    }
                    is Detail -> NavEntry(key) {
                        DetailRoute(
                            modifier = Modifier.padding(innerPadding),
                            movieId = key.movieId,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    else -> throw IllegalArgumentException("Unknown key: $key")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PortofolioTMDBAppTheme {
        Navigation()
    }
}
