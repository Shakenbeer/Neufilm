package com.shakenbeer.neufilm.data

import com.shakenbeer.neufilm.data.api.ApiCast
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.suspendedApiCall
import com.shakenbeer.neufilm.data.preload.Configuration
import com.shakenbeer.neufilm.domain.entity.Actor
import com.shakenbeer.neufilm.domain.repo.ActorRepo
import javax.inject.Inject

class ActorRepoImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val configuration: Configuration
) : ActorRepo {

    override suspend fun getActors(movieId: Int): Result<List<Actor>> =
        suspendedApiCall({ movieApi.getCast(movieId) }, { it }).let { result ->
            when {
                result.isSuccess -> Result.success(result.getOrNull()?.let { map(it) }
                    ?: emptyList())
                result.isFailure -> Result.failure(result.exceptionOrNull() ?: Throwable())
                else -> throw IllegalStateException()
            }
        }

    private fun map(apiCast: ApiCast): List<Actor>? {
        val imageBaseUrl = configuration.imageBaseUrl
        val profileSize = configuration.profileSize
        return apiCast.cast?.mapNotNull { apiActor ->
            with(apiActor) {
                if (id != null && order != null &&
                    name != null && name.isNotBlank() &&
                    profilePath != null && profilePath.isNotBlank()
                ) {
                    Actor(
                        id = id,
                        name = name,
                        order = order,
                        profileUrl = "$imageBaseUrl$profileSize/$profilePath"
                    )
                } else {
                    null
                }
            }
        }
    }
}