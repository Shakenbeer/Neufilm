package com.shakenbeer.neufilm.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("configuration")
    suspend fun getConfiguration(): Response<ApiConfiguration>

    @GET("genre/movie/list")
    suspend fun getGenres(): Response<ApiGenres>

    @GET("movie/now_playing")
    suspend fun getLatestMovies(@Query("page") page: Int): Response<ApiLatestMovies>

    @GET("movie/{movie_id}/credits")
    suspend fun getCast(@Path("movie_id") movieId: Int): Response<ApiCast>
}