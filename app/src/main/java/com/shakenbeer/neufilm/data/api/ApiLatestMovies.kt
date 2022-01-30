package com.shakenbeer.neufilm.data.api

import com.google.gson.annotations.SerializedName

data class ApiLatestMovies(

    @field:SerializedName("results")
    val results: List<ApiMovie>?
)

data class ApiMovie(

    @field:SerializedName("title")
    val title: String?,

    @field:SerializedName("genre_ids")
    val genreIds: List<Int>?,

    @field:SerializedName("poster_path")
    val posterPath: String?,

    @field:SerializedName("overview")
    val overview: String?,

    @field:SerializedName("vote_average")
    val voteAverage: Double?,

    @field:SerializedName("id")
    val id: Int?
)
