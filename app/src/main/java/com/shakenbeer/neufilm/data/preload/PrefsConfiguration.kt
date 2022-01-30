package com.shakenbeer.neufilm.data.preload

import com.shakenbeer.neufilm.data.api.ApiConfiguration
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.suspendedApiCall
import com.shakenbeer.neufilm.data.prefs.SharedPrefs
import com.shakenbeer.neufilm.di.ConfigPrefs
import com.shakenbeer.neufilm.di.DispatchersIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PrefsConfiguration @Inject constructor(
    private val movieApi: MovieApi,
    @ConfigPrefs private val prefs: SharedPrefs,
    @DispatchersIO private val ioScope: CoroutineScope
) : Configuration {

    override val imageBaseUrl get() = prefs.get(KEY_IMAGE_URL, FALLBACK_IMAGE_URL)
    override val posterSizeList get() = prefs.get(KEY_POSTER_SIZE_LIST, FALLBACK_POSTER_SIZE_LIST)
    override val posterSizeDetails
        get() = prefs.get(
            KEY_POSTER_SIZE_DETAILS,
            FALLBACK_POSTER_SIZE_DETAILS
        )
    override val profileSize get() = prefs.get(KEY_PROFILE_SIZE, FALLBACK_PROFILE_SIZE)

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        ioScope.launch {
            suspendedApiCall(movieApi::getConfiguration) { it }.also { result ->
                if (result.isSuccess) {
                    saveConfiguration(result.getOrNull())
                }
            }
        }
    }

    private fun saveConfiguration(apiConfiguration: ApiConfiguration?) {
        apiConfiguration?.images?.run {
            secureBaseUrl?.let { prefs.store(KEY_IMAGE_URL, it) }
            posterSizes?.storeDesiredSize(KEY_POSTER_SIZE_LIST, DESIRED_POSTER_LIST_SIZE)
            posterSizes?.storeDesiredSize(KEY_POSTER_SIZE_DETAILS, DESIRED_POSTER_DETAILS_SIZE)
            profileSizes?.storeDesiredSize(KEY_PROFILE_SIZE, DESIRED_PROFILE_SIZE)
        }
    }

    private fun List<String>.storeDesiredSize(key: String, desiredSize: Int) {
            asSequence()
                .filter { it.startsWith('w') }
                .mapNotNull { it.substring(1).toIntOrNull() }
                .sorted()
                .lastOrNull { it <= desiredSize }?.let { width ->
                    prefs.store(key, "w$width")
                }
        }


    companion object {
        const val KEY_IMAGE_URL = "image_url"
        const val KEY_POSTER_SIZE_LIST = "poster_size_list"
        const val KEY_POSTER_SIZE_DETAILS = "poster_size_details"
        const val KEY_PROFILE_SIZE = "profile_size"
        const val FALLBACK_IMAGE_URL = "http://image.tmdb.org/t/p/"
        const val FALLBACK_POSTER_SIZE_LIST = "w342"
        const val FALLBACK_POSTER_SIZE_DETAILS = "w780"
        const val FALLBACK_PROFILE_SIZE = "w185"
        const val DESIRED_POSTER_LIST_SIZE = 342
        const val DESIRED_POSTER_DETAILS_SIZE = 780
        const val DESIRED_PROFILE_SIZE = 185
    }
}