package com.shakenbeer.neufilm.data.preload

import com.shakenbeer.neufilm.data.api.ApiGenres
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.suspendedApiCall
import com.shakenbeer.neufilm.domain.entity.Genre
import com.shakenbeer.neufilm.domain.repo.Genres
import javax.inject.Inject

class InMemoryGenres  @Inject constructor(private val movieApi: MovieApi) : Genres {

    lateinit var genres: List<Genre>

    override suspend fun get(): List<Genre> {
        if (!::genres.isInitialized) {
            suspendedApiCall(movieApi::getGenres, ::mapGenres).also { result ->
                genres = if (result.isSuccess) {
                    result.getOrDefault(emptyList())
                } else {
                    emptyList()
                }
            }
        }
        return genres
    }

    private fun mapGenres(apiGenres: ApiGenres) =
        apiGenres.genres?.mapNotNull { genre ->
            if (genre.id != null && genre.name != null) {
                Genre(genre.id, genre.name)
            } else {
                null
            }
        } ?: emptyList()
}