package com.harun.tmdbapp.data.remote.retrofit.network

import com.harun.tmdbapp.data.remote.retrofit.interceptor.RequestInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object Network {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .addInterceptor(RequestInterceptor().loggingInterceptor)
        .addInterceptor(RequestInterceptor().requestInterceptor)
        .build()
    private val retrofit = Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        addConverterFactory(json.asConverterFactory(contentType))
        client(client)
    }.build()

    val api: ApiService by lazy { retrofit.create(ApiService::class.java) }
}