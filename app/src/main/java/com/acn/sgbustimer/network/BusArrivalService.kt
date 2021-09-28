package com.acn.sgbustimer.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import com.acn.sgbustimer.model.*
import retrofit2.Call

interface BusArrivalService {
    @GET("BusArrivalv2")
    suspend fun getBusArrivalAsync(@Query("BusStopCode") busStopCode: String): Response<BusArrival>
}