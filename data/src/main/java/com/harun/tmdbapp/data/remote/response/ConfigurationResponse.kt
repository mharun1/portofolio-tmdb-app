package com.harun.tmdbapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigurationResponse(
    val images: ConfigurationDto
)

@Serializable
data class ConfigurationDto(
    @SerialName("secure_base_url")
    val baseUrl: String,
    @SerialName("poster_sizes")
    val posterSize: List<String>
)
