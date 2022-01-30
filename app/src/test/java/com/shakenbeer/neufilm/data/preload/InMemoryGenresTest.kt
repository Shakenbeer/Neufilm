package com.shakenbeer.neufilm.data.preload

import com.shakenbeer.neufilm.data.api.ApiGenre
import com.shakenbeer.neufilm.data.api.ApiGenres
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.errorJson
import com.shakenbeer.neufilm.data.jsonMediaType
import com.shakenbeer.neufilm.domain.entity.Genre
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class InMemoryGenresTest {

    @Mock
    private lateinit var movieApi: MovieApi

    @Mock
    private lateinit var inMemoryGenres: InMemoryGenres

    @Before
    fun setUp() {
        inMemoryGenres = InMemoryGenres(movieApi)
    }

    @Test
    fun `if api returns error then genres list is empty`() = runTest {
        whenever(movieApi.getGenres())
            .thenReturn(Response.error(400, errorJson.toResponseBody(jsonMediaType)))

        assertTrue(inMemoryGenres.get().isEmpty())
    }

    @Test
    fun `genre without id should be skipped`()  = runTest {
        whenever(movieApi.getGenres())
            .thenReturn(Response.success(
                ApiGenres(listOf(ApiGenre(null, "name1")))
            ))

        assertTrue(inMemoryGenres.get().isEmpty())
    }

    @Test
    fun `genre without name should be skipped`()  = runTest {
        whenever(movieApi.getGenres())
            .thenReturn(Response.success(
                ApiGenres(listOf(ApiGenre(1, null)))
            ))

        assertTrue(inMemoryGenres.get().isEmpty())
    }

    @Test
    fun `check genres mapping`()  = runTest {
        whenever(movieApi.getGenres())
            .thenReturn(Response.success(
                ApiGenres(listOf(ApiGenre(1, "name1"), ApiGenre(2, "name2")))
            ))

        val genres = inMemoryGenres.get()
        assertEquals(2, genres.size)
        assertEquals(Genre(1, "name1"), genres[0])
        assertEquals(Genre(2, "name2"), genres[1])
    }
}