package com.harun.tmdbapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home: NavKey

@Serializable
data class Detail(val movieId: Int): NavKey

@Serializable
data object Search: NavKey