package com.acn.sgbustimer.repository

import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.network.WebAccess

class BusNearbyRepository {

    suspend fun getBusArrivalAsync(busStopCode: String): BusArrival? {
        val request = WebAccess.busArrivalApiClient.getBusArrivalAsync(busStopCode)

        if(request.isSuccessful){
            request.body().let {
                return it
            }
        }

        return null
    }

}