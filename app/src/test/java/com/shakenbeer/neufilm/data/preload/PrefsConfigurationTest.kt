package com.shakenbeer.neufilm.data.preload

import com.shakenbeer.neufilm.data.api.ApiConfiguration
import com.shakenbeer.neufilm.data.api.ApiImages
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.errorJson
import com.shakenbeer.neufilm.data.jsonMediaType
import com.shakenbeer.neufilm.data.prefs.SharedPrefs
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.FALLBACK_IMAGE_URL
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.FALLBACK_POSTER_SIZE_DETAILS
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.FALLBACK_POSTER_SIZE_LIST
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.FALLBACK_PROFILE_SIZE
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.KEY_IMAGE_URL
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.KEY_POSTER_SIZE_DETAILS
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.KEY_POSTER_SIZE_LIST
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration.Companion.KEY_PROFILE_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class PrefsConfigurationTest {

    @Mock
    private lateinit var movieApi: MovieApi

    @Mock
    private lateinit var prefs: SharedPrefs

    private lateinit var prefsConfiguration: PrefsConfiguration

    private val scope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        whenever(prefs.get(KEY_IMAGE_URL, FALLBACK_IMAGE_URL)).thenReturn(FALLBACK_IMAGE_URL)
        whenever(prefs.get(KEY_POSTER_SIZE_LIST, FALLBACK_POSTER_SIZE_LIST)).thenReturn(
            FALLBACK_POSTER_SIZE_LIST
        )
        whenever(prefs.get(KEY_POSTER_SIZE_DETAILS, FALLBACK_POSTER_SIZE_DETAILS)).thenReturn(
            FALLBACK_POSTER_SIZE_DETAILS
        )
        whenever(prefs.get(KEY_PROFILE_SIZE, FALLBACK_PROFILE_SIZE)).thenReturn(
            FALLBACK_PROFILE_SIZE
        )
    }

    @Test
    fun `if api returns error then fallback values are used`() = runTest {
        whenever(movieApi.getConfiguration()).thenReturn(
            Response.error(400, errorJson.toResponseBody(jsonMediaType))
        )

        prefsConfiguration = PrefsConfiguration(movieApi, prefs, scope)

        assertEquals(FALLBACK_IMAGE_URL, prefsConfiguration.imageBaseUrl)
        assertEquals(FALLBACK_POSTER_SIZE_LIST, prefsConfiguration.posterSizeList)
        assertEquals(FALLBACK_POSTER_SIZE_DETAILS, prefsConfiguration.posterSizeDetails)
        assertEquals(FALLBACK_PROFILE_SIZE, prefsConfiguration.profileSize)
    }

    @Test
    fun `if api returns image url, then image url saved`() = runTest {
        val secureBaseUrl = "https://helloworld.com"
        whenever(movieApi.getConfiguration()).thenReturn(
            Response.success(
                ApiConfiguration(
                    ApiImages(
                        secureBaseUrl = secureBaseUrl,
                        posterSizes = null,
                        profileSizes = null
                    )
                )
            )
        )

        prefsConfiguration = PrefsConfiguration(movieApi, prefs, scope)

        verify(prefs).store(KEY_IMAGE_URL, secureBaseUrl)
    }

    @Test
    fun `if api return poster sizes, then closest to desired poster list size is saved`() = runTest {
        whenever(movieApi.getConfiguration()).thenReturn(
            Response.success(
                ApiConfiguration(
                    ApiImages(
                        posterSizes = listOf("w180", "h250", "w290", "default", "w280", "original", "w150"),
                        profileSizes = null,
                        secureBaseUrl = null
                    )
                )
            )
        )

        prefsConfiguration = PrefsConfiguration(movieApi, prefs, scope)

        verify(prefs).store(KEY_POSTER_SIZE_LIST, "w290")
    }

    @Test
    fun `if api return poster sizes, then closest to desired poster details size is saved`() = runTest {
        whenever(movieApi.getConfiguration()).thenReturn(
            Response.success(
                ApiConfiguration(
                    ApiImages(
                        posterSizes = listOf("w180", "h250", "w290", "default", "w280", "original", "w150"),
                        profileSizes = null,
                        secureBaseUrl = null
                    )
                )
            )
        )

        prefsConfiguration = PrefsConfiguration(movieApi, prefs, scope)

        verify(prefs).store(KEY_POSTER_SIZE_DETAILS, "w290")
    }

    @Test
    fun `if api return profile sizes, then closest to desired profile size is saved`() = runTest {
        whenever(movieApi.getConfiguration()).thenReturn(
            Response.success(
                ApiConfiguration(
                    ApiImages(
                        profileSizes = listOf("w180", "h250", "w290", "default", "w280", "original", "w150"),
                        posterSizes = null,
                        secureBaseUrl = null
                    )
                )
            )
        )

        prefsConfiguration = PrefsConfiguration(movieApi, prefs, scope)

        verify(prefs).store(KEY_PROFILE_SIZE, "w180")
    }
}