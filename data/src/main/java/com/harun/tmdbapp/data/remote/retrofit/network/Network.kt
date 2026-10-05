package com.harun.tmdbapp.data.remote.retrofit.network

import com.harun.tmdbapp.data.BuildConfig
import com.harun.tmdbapp.data.remote.retrofit.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object Network {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val TOKEN = BuildConfig.TMDB_ACCESS_TOKEN
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(TOKEN))
        .addInterceptor(loggingInterceptor)
        .build()
        
    private val retrofit = Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        addConverterFactory(json.asConverterFactory(contentType))
        client(client)
    }.build()

    val api: ApiService by lazy { retrofit.create(ApiService::class.java) }
}