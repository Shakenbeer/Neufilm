package com.shakenbeer.neufilm.data.api

import com.google.gson.annotations.SerializedName

data class ApiConfiguration(

    @field:SerializedName("images")
    val images: ApiImages?
)

data class ApiImages(

    @field:SerializedName("poster_sizes")
    val posterSizes: List<String>?,

    @field:SerializedName("profile_sizes")
    val profileSizes: List<String>?,

    @field:SerializedName("secure_base_url")
    val secureBaseUrl: String?
)
