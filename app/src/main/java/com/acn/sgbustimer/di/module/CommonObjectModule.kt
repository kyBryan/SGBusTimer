package com.acn.sgbustimer.di.module

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonObjectModule {

    @Provides
    @Singleton
    @Named(BASE_OKHTTP_CLIENT)
    fun provideBaseOKHttpClient(): OkHttpClient {

        // Header Required for API
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("AccountKey", DATAMALL_API_KEY)
                    .build()
                chain.proceed(request)
            }

        return httpClient.build()
    }

    @Provides
    @Singleton
    @Named(MOSHI_CONVERTER)
    fun provideMoshiConverter(): Moshi {
        return Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    @Named(COMMON_REST_ADAPTER)
    fun provideCommonRestAdapter(
        @Named(BASE_OKHTTP_CLIENT) okHttpClient: OkHttpClient,
        @Named(MOSHI_CONVERTER) moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DATAMALL_API)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    const val BASE_OKHTTP_CLIENT = "BASE_OKHTTP_CLIENT"
    const val MOSHI_CONVERTER = "MOSHI_CONVERTER"
    const val COMMON_REST_ADAPTER = "COMMON_REST_ADAPTER"
    const val DATAMALL_API = "http://datamall2.mytransport.sg/ltaodataservice/"
    const val DATAMALL_API_KEY = "r6LVWluBQXC0hv3Hjc3OOg=="
}