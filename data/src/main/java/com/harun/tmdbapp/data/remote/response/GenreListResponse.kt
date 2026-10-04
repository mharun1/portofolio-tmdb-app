package com.harun.tmdbapp.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class GenreListResponse(
    val genres: List<GenreDto>
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String,
)
