package com.shakenbeer.neufilm.data

import com.shakenbeer.neufilm.data.api.ApiLatestMovies
import com.shakenbeer.neufilm.data.api.ApiMovie
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.ApiException
import com.shakenbeer.neufilm.data.preload.Configuration
import com.shakenbeer.neufilm.domain.entity.Genre
import com.shakenbeer.neufilm.domain.repo.Genres
import com.shakenbeer.neufilm.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class MovieRepoImplTest {

    @Mock
    private lateinit var movieApi: MovieApi

    private val genres = object : Genres {
        override suspend fun get() = listOf(
            Genre(1, "Action"), Genre(2, "Drama"), Genre(3, "Comedy")
        )
    }

    private val configuration = object : Configuration {
        override val imageBaseUrl = "http://image.tmdb.org/t/p/"
        override val posterSizeList = "w300"
        override val posterSizeDetails = "w780"
        override val profileSize = "w185"
    }

    private lateinit var movieRepoImpl: MovieRepoImpl

    @Before
    fun setUp() = runTest {
        movieRepoImpl = MovieRepoImpl(movieApi, genres, configuration)
    }

    @Test
    fun `if api returns error then repo returns failure`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.error(400, errorJson.toResponseBody(jsonMediaType))
        )

        val result = movieRepoImpl.getLatestMovies(1)

        assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()

        assertNotNull(exception)
        assertTrue(exception is ApiException)
        assertEquals("The resource you requested could not be found.", exception?.message)
    }

    @Test
    fun `movie without id should be skipped`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(id = null, title = "Hello, World", posterPath = "hello_world.jpg")
                )
            )
        )

        assertEmptyResult(movieRepoImpl.getLatestMovies(1))
    }

    @Test
    fun `movie without title should be skipped`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(id = 1, title = null, posterPath = "hello_world.jpg")
                )
            )
        )

        assertEmptyResult(movieRepoImpl.getLatestMovies(1))
    }

    @Test
    fun `movie with blank title should be skipped`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(id = 1, title = "   ", posterPath = "hello_world.jpg")
                )
            )
        )

        assertEmptyResult(movieRepoImpl.getLatestMovies(1))
    }

    @Test
    fun `movie without poster should be skipped`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(id = 1, title = "Hello, World", posterPath = null)
                )
            )
        )

        assertEmptyResult(movieRepoImpl.getLatestMovies(1))
    }

    @Test
    fun `movie with blank poster should be skipped`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(id = 1, title = "Hello, World", posterPath = "   ")
                )
            )
        )

        assertEmptyResult(movieRepoImpl.getLatestMovies(1))
    }

    @Test
    fun `check proper conversion`() = runTest {
        whenever(movieApi.getLatestMovies(any())).thenReturn(
            Response.success(
                responseBody(
                    apiMovie(
                        id = 1,
                        title = "Hello, World",
                        posterPath = "hello_world.jpg",
                        overview = "Hello, world",
                        voteAverage = 9.9,
                        genreIds = listOf(1, 3)
                    ),
                    apiMovie(id = 2, title = "Hello, World 2", posterPath = "hello_world2.jpg")
                )
            )
        )

        val result = movieRepoImpl.getLatestMovies(1)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())

        val movies = result.getOrNull()!!
        assertEquals(2, movies.size)

        assertEquals("http://image.tmdb.org/t/p/w300/hello_world.jpg", movies.first().listPosterUrl)
        assertEquals("http://image.tmdb.org/t/p/w780/hello_world.jpg", movies.first().detailsPosterUrl)
        assertEquals(9.9, movies.first().averageVote, 0.01)
        assertEquals(listOf(Genre(1, "Action"), Genre(3, "Comedy")), movies.first().genres)

        assertTrue(movies.last().overview.isEmpty())
        assertEquals(0.0, movies.last().averageVote, 0.01)
        assertTrue(movies.last().genres.isEmpty())
    }

    private fun assertEmptyResult(result: Result<List<Movie>>) {
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertTrue(result.getOrNull()?.isEmpty() ?: false)
    }

    private fun responseBody(vararg apiMovies: ApiMovie) = ApiLatestMovies(
        listOf(*apiMovies)
    )

    private fun apiMovie(
        title: String? = null,
        genreIds: List<Int>? = null,
        posterPath: String? = null,
        overview: String? = null,
        voteAverage: Double? = null,
        id: Int? = null
    ) = ApiMovie(
        title,
        genreIds,
        posterPath,
        overview,
        voteAverage,
        id
    )

}