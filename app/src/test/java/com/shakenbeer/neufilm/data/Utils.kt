package com.shakenbeer.neufilm.data

import okhttp3.MediaType.Companion.toMediaTypeOrNull

val errorJson = """
{
  "status_message": "The resource you requested could not be found.",
  "status_code": 34
}    
""".trimIndent()

val jsonMediaType = "application/json".toMediaTypeOrNull()