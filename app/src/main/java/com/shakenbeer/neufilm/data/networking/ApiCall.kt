package com.shakenbeer.neufilm.data.networking

import com.google.gson.Gson
import com.shakenbeer.neufilm.data.api.ApiError
import retrofit2.Response

suspend inline fun <T, R> suspendedApiCall(
    crossinline apiCall: suspend () -> Response<T>,
    noinline transform: (T) -> (R)
): Result<R> {
    return try {
        val response = apiCall()
        val body = response.body()
        if (response.isSuccessful) {
            processApiSuccess<R, T>(body, transform)
        } else {
            processApiError(response)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

fun <R, T> processApiSuccess(body: T?, transform: (T) -> R) = if (body != null) {
    Result.success(transform(body))
} else {
    Result.failure(ApiException("Request was successful, but response body is null"))
}

fun <R, T> processApiError(response: Response<T>): Result<R> = try {
    val errorBody = response.errorBody()?.string()
    Gson().fromJson(errorBody, ApiError::class.java)
} catch (e: Exception) {
    null
}?.statusMessage?.let { statusMessage ->
    Result.failure(ApiException(statusMessage))
} ?: Result.failure(ApiException())

