package com.shakenbeer.neufilm.data

import com.shakenbeer.neufilm.data.ActorRepoImpl
import com.shakenbeer.neufilm.data.api.ApiActor
import com.shakenbeer.neufilm.data.api.ApiCast
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.errorJson
import com.shakenbeer.neufilm.data.jsonMediaType
import com.shakenbeer.neufilm.data.networking.ApiException
import com.shakenbeer.neufilm.data.preload.Configuration
import com.shakenbeer.neufilm.domain.entity.Actor
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class ActorRepoImplTest {

    @Mock
    private lateinit var movieApi: MovieApi

    private val configuration = object : Configuration {
        override val imageBaseUrl = "http://image.tmdb.org/t/p/"
        override val posterSizeList = "w300"
        override val posterSizeDetails = "w780"
        override val profileSize = "w185"
    }

    private lateinit var actorRepoImpl: ActorRepoImpl

    @Before
    fun setUp() = runTest {
        actorRepoImpl = ActorRepoImpl(movieApi, configuration)
    }

    @Test
    fun `if api returns error then repo returns failure`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.error(400, errorJson.toResponseBody(jsonMediaType))
        )

        val result = actorRepoImpl.getActors(1)

        Assert.assertTrue(result.isFailure)

        val exception = result.exceptionOrNull()

        Assert.assertNotNull(exception)
        Assert.assertTrue(exception is ApiException)
        Assert.assertEquals("The resource you requested could not be found.", exception?.message)
    }

    @Test
    fun `actor without id should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = null, name = "John Dow", profilePath = "john_dow.jpg", order = 1)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    @Test
    fun `actor without name should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = 1, name = null, profilePath = "john_dow.jpg", order = 1)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    @Test
    fun `actor with blank name should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = 1, name = "   ", profilePath = "john_dow.jpg", order = 1)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    @Test
    fun `actor without profile picture should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = 1, name = "John Dow", profilePath = null, order = 1)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    @Test
    fun `actor with blank profile picture should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = 1, name = "John Dow", profilePath = "   ", order = 1)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    @Test
    fun `actor without order should be skipped`() = runTest {
        whenever(movieApi.getCast(any())).thenReturn(
            Response.success(
                responseBody(
                    apiActor(id = 1, name = "John Dow", profilePath = "john_dow.jpg", order = null)
                )
            )
        )

        assertEmptyResult(actorRepoImpl.getActors(1))
    }

    private fun assertEmptyResult(result: Result<List<Actor>>) {
        Assert.assertTrue(result.isSuccess)
        Assert.assertNotNull(result.getOrNull())
        Assert.assertTrue(result.getOrNull()?.isEmpty() ?: false)
    }

    private fun responseBody(vararg apiActors: ApiActor) = ApiCast(
        listOf(*apiActors)
    )

    private fun apiActor(
        name: String? = null,
        profilePath: String? = null,
        id: Int? = null,
        order: Int? = null
    ) = ApiActor(
        name,
        profilePath,
        id,
        order
    )

}