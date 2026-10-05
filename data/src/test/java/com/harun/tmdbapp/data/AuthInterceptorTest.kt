package com.harun.tmdbapp.data

import com.harun.tmdbapp.data.remote.retrofit.interceptor.AuthInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `interceptor adds authorization header`() {
        server.enqueue(MockResponse().setResponseCode(200))

        val authInterceptor = AuthInterceptor("test-token")

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/"))
            .build()

        client.newCall(request).execute().use {
            val recordedRequest = server.takeRequest()
            assertEquals("Bearer test-token", recordedRequest.getHeader("Authorization"))
        }

    }
}