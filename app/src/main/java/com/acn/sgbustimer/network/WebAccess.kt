package com.acn.sgbustimer.network

import com.acn.sgbustimer.util.Constant
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object WebAccess {

    // Header Required for API
    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("AccountKey", Constant.DATAMALL_API_KEY)
                .build()
            chain.proceed(request)
        }.build()

    // Moshi Converter
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Retrofit2 Builder
    val rfDataMall = Retrofit.Builder()
        .baseUrl(Constant.DATAMALL_API)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    // API Services
    val dataMallService: DataMallService by lazy {
        rfDataMall.build()
            .create(DataMallService::class.java)
    }
}
