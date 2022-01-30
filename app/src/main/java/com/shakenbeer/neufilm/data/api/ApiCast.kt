package com.shakenbeer.neufilm.data.api

import com.google.gson.annotations.SerializedName

data class ApiCast(

    @field:SerializedName("cast")
    val cast: List<ApiActor>?
)

data class ApiActor(

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("profile_path")
    val profilePath: String?,

    @field:SerializedName("id")
    val id: Int?,

    @field:SerializedName("order")
    val order: Int?
)
