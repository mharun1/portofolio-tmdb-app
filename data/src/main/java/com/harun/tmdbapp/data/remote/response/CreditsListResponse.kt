package com.harun.tmdbapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsListResponse(
    val cast: List<CastDto> = emptyList()
)

@Serializable
data class CastDto(
    val id: Int,
    val name: String,
    val character: String,
    @SerialName("profile_path")
    val picture: String? = null,
)