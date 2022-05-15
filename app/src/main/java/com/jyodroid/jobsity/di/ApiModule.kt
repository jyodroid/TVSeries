package com.jyodroid.jobsity.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jyodroid.jobsity.BuildConfig
import com.jyodroid.jobsity.api.EpisodeService
import com.jyodroid.jobsity.api.SeriesService
import com.jyodroid.jobsity.api.networkresponse.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    private val timeout = 30L
    private val json: Json = Json {
        ignoreUnknownKeys = true
    }

    @ExperimentalSerializationApi
    private val jsonConverter = json.asConverterFactory("application/json".toMediaType())

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .build()
    }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(jsonConverter)
            .baseUrl(BuildConfig.HOST_API)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideSeriesService(retrofit: Retrofit): SeriesService =
        retrofit.create(SeriesService::class.java)

    @Singleton
    @Provides
    fun provideEpisodesService(retrofit: Retrofit): EpisodeService =
        retrofit.create(EpisodeService::class.java)
}