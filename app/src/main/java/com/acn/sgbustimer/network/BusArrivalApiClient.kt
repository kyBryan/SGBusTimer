package com.acn.sgbustimer.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*
import com.acn.sgbustimer.model.*

interface BusArrivalApiClient {
    @GET("BusArrivalv2") fun getBusArrivalAsync(@Path("BusStopCode") busStopCode: String): Deferred<Response<List<BusArrival>>>
}