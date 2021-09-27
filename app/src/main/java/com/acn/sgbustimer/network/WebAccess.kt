package com.acn.sgbustimer.network

import android.content.res.Resources
import android.util.Log
import com.acn.sgbustimer.R
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import okhttp3.Interceptor

import okhttp3.OkHttpClient
import okhttp3.Request


object WebAccess {
    val busArrivalApi : BusArrivalApiClient by lazy {
        Timber.i("Creating retrofit client")

        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                    .addHeader("AccountKey", Resources.getSystem().getString(R.string.lta_datamall_key))
                    .build()
                chain.proceed(request)
            }.build()

        val retrofit = Retrofit.Builder()
            // The  address routes request from the Android emulator
            // to the lta datamall of the host PC
            .baseUrl(Resources.getSystem().getString(R.string.lta_datamall))
            // Client
            .client(httpClient)
            // Moshi maps JSON to classes
            .addConverterFactory(MoshiConverterFactory.create())
            // The call adapter handles threads
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        // Create Retrofit client
        return@lazy retrofit.create(BusArrivalApiClient::class.java)
    }

}