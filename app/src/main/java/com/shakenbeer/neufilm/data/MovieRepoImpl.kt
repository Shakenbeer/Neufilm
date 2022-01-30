package com.shakenbeer.neufilm.data

import com.shakenbeer.neufilm.data.api.ApiLatestMovies
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.suspendedApiCall
import com.shakenbeer.neufilm.data.preload.Configuration
import com.shakenbeer.neufilm.domain.entity.Movie
import com.shakenbeer.neufilm.domain.repo.Genres
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import javax.inject.Inject

class MovieRepoImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val genres: Genres,
    private val configuration: Configuration
) : MovieRepo {

    private var selectedMovie: Movie? = null

    override suspend fun getLatestMovies(page: Int): Result<List<Movie>> =
        suspendedApiCall({ movieApi.getLatestMovies(page) }, { it }).let { result ->
            when {
                result.isSuccess -> Result.success(result.getOrNull()?.let { map(it) }
                    ?: emptyList())
                result.isFailure -> Result.failure(result.exceptionOrNull() ?: Throwable())
                else -> throw IllegalStateException()
            }

        }

    override fun setSelectedMovie(movie: Movie) {
        selectedMovie = movie
    }

    override fun getSelectedMovie(): Result<Movie> =
        selectedMovie?.let { Result.success(it) } ?: Result.failure(Throwable())

    private suspend fun map(apiLatestMovies: ApiLatestMovies): List<Movie>? {
        val imageBaseUrl = configuration.imageBaseUrl
        val listPosterSize = configuration.posterSizeList
        val detailsPosterSize = configuration.posterSizeDetails
        val genres = genres.get()
        return apiLatestMovies.results?.mapNotNull { apiMovie ->
            with(apiMovie) {
                if (id != null &&
                    title != null && title.isNotBlank() &&
                    posterPath != null && posterPath.isNotBlank()
                ) {
                    val genreIds = genreIds ?: emptyList()
                    Movie(
                        id = id,
                        title = title,
                        overview = overview ?: "",
                        listPosterUrl = "$imageBaseUrl$listPosterSize/$posterPath",
                        detailsPosterUrl = "$imageBaseUrl$detailsPosterSize/$posterPath",
                        averageVote = voteAverage ?: 0.0,
                        genres = genres.filter { genre -> genre.id in genreIds }
                    )
                } else {
                    null
                }
            }
        }
    }
}