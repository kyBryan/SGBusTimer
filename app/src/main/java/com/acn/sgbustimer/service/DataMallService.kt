package com.acn.sgbustimer.service

import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStops
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DataMallService {
    @GET("BusArrivalv2")
    suspend fun getBusArrivalApi(@Query("BusStopCode") busStopCode: String): Response<BusArrival>

    @GET("BusStops")
    suspend fun getBusStopsApi(@Query("\$skip") skip: String): Response<BusStops>
}
