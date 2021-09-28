package com.acn.sgbustimer.network

import android.util.Log
import com.acn.sgbustimer.util.Constant
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import okhttp3.OkHttpClient
import okhttp3.Request


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

    val busArrivalService : BusArrivalService by lazy {
        Log.i("BusArrivalApiClient","Creating retrofit client")

        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            // The  address routes request from the Android emulator
            // to the lta datamall of the host PC
            .baseUrl(Constant.DATAMALL_API)
            // Client
            .client(httpClient)
            // Moshi maps JSON to classes
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            // The call adapter handles threads
            //.addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        // Create Retrofit client
        return@lazy retrofit.create(BusArrivalService::class.java)
    }

    val busArrivalApiClient = ApiClient(busArrivalService)

}