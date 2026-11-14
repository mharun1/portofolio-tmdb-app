package com.harun.tmdbapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface TopLevelDestination : NavKey {
    val defaultIcon: ImageVector
    val selectedIcon: ImageVector
    val label: String
}

@Serializable
data object Home : TopLevelDestination {
    override val defaultIcon: ImageVector = Icons.Outlined.Home
    override val selectedIcon: ImageVector = Icons.Default.Home
    override val label: String = "Home"
}

@Serializable
data object Bookmark : TopLevelDestination {
    override val defaultIcon: ImageVector = Icons.Outlined.BookmarkBorder
    override val selectedIcon: ImageVector = Icons.Default.Bookmark
    override val label: String = "Bookmark"
}

@Serializable
data object Profile : TopLevelDestination {
    override val defaultIcon: ImageVector = Icons.Outlined.Person
    override val selectedIcon: ImageVector = Icons.Default.Person
    override val label: String = "Profile"

}

@Serializable
data object Search: NavKey

@Serializable
data class Detail(val movieId: Int): NavKey

val TOP_LEVEL_DESTINATION = listOf(Home, Bookmark, Profile)