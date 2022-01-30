package com.shakenbeer.neufilm.data.networking

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.request().let { request ->
        val newUrl = request.url.newBuilder()
            .addQueryParameter("api_key", apiKey).build()
        val newRequest = request.newBuilder().url(newUrl).build()
        chain.proceed(newRequest)
    }
}