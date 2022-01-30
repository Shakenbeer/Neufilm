package com.shakenbeer.neufilm.data.api

import com.google.gson.annotations.SerializedName

data class ApiError(

	@field:SerializedName("status_message")
	val statusMessage: String? = null,

	@field:SerializedName("status_code")
	val statusCode: Int? = null
)
