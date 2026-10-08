package com.harun.tmdbapp.data.helper

private const val BASE_URL = "https://image.tmdb.org/t/p/"

fun String?.toUrl(size: String): String? =
    this?.let { "$BASE_URL$size$it" }