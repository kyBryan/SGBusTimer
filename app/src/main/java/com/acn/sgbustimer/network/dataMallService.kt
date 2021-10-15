package com.acn.sgbustimer.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import com.acn.sgbustimer.model.*
import retrofit2.Call

interface DataMallService {
    @GET("BusArrivalv2")
    suspend fun getBusArrivalApi(@Query("BusStopCode") busStopCode: String): Response<BusArrival>

    @GET("BusStops")
    suspend fun getBusStopsApi(@Query("skip") skip: String): Response<BusStops>
}