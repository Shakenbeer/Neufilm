package com.shakenbeer.neufilm.di

import android.content.Context
import com.shakenbeer.neufilm.BuildConfig
import com.shakenbeer.neufilm.data.ActorRepoImpl
import com.shakenbeer.neufilm.data.MovieRepoImpl
import com.shakenbeer.neufilm.data.api.BASE_URL
import com.shakenbeer.neufilm.data.api.MovieApi
import com.shakenbeer.neufilm.data.networking.ApiKeyInterceptor
import com.shakenbeer.neufilm.data.prefs.SharedPrefs
import com.shakenbeer.neufilm.data.preload.Configuration
import com.shakenbeer.neufilm.data.preload.InMemoryGenres
import com.shakenbeer.neufilm.data.preload.PrefsConfiguration
import com.shakenbeer.neufilm.domain.repo.ActorRepo
import com.shakenbeer.neufilm.domain.repo.Genres
import com.shakenbeer.neufilm.domain.repo.MovieRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatchersIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ConfigPrefs

@Module
@InstallIn(SingletonComponent::class)
object Prefs {

    @Provides
    @Singleton
    @ConfigPrefs
    fun provideSharedPrefs(@ApplicationContext context: Context) =
        SharedPrefs(context, "configuration")

    @Provides
    @DispatchersIO
    fun provideIoScope() = CoroutineScope(Job() + Dispatchers.IO)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class Preload {

    @Binds
    @Singleton
    abstract fun bindConfiguration(prefsConfiguration: PrefsConfiguration): Configuration

    @Binds
    @Singleton
    abstract fun bindGenres(inMemoryGenres: InMemoryGenres): Genres
}

@Module
@InstallIn(SingletonComponent::class)
abstract class Repo {

    @Binds
    @Singleton
    abstract fun bindMovieRepo(movieRepoImpl: MovieRepoImpl): MovieRepo

    @Binds
    @Singleton
    abstract fun bindActorRepo(actorRepoImpl: ActorRepoImpl): ActorRepo
}

@Module
@InstallIn(SingletonComponent::class)
object Network {

    private fun provideOkHttpClient() = OkHttpClient.Builder().run {
        addInterceptor(ApiKeyInterceptor(BuildConfig.API_KEY))
        if (BuildConfig.DEBUG) {
            addInterceptor(
                HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            )
        }
        build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
object API {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): MovieApi = retrofit.create(MovieApi::class.java)
}


