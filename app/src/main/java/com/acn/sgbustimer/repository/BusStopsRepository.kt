package com.acn.sgbustimer.repository

import android.location.Location
import android.util.Log
import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.network.WebAccess
import com.acn.sgbustimer.util.Constant
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class BusStopsRepository {
    var cJob: CompletableJob? = null

    fun getBusStopsValue(userCurrentLocation: Location): ArrayList<BusStopsValue> {
        var arrListOfNBBusStops = ArrayList<BusStopsValue>()

        cJob = Job()
        Log.i("BusStopsRepository","Entered BSR 1")
        cJob?.let{ job ->
            Log.i("BusStopsRepository","Entered BSR 2")
            CoroutineScope(Dispatchers.IO + job).launch {
                Log.i("BusStopsRepository","Entered BSR 3")
                var skipStringList = listOf("0", "500", "1000", "1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000")
                val listOfBusStops: MutableList<BusStopsValue> = mutableListOf()
                var bsvLocation = Location("")

                for (skipStr in skipStringList) {
                    val busStops = WebAccess.dataMallService.getBusStopsApi(skipStr)

                    busStops.body()?.let{
                        listOfBusStops += it.value
                    }
                }

                for (busStopsValue in listOfBusStops) {
                    bsvLocation.latitude = busStopsValue.Latitude
                    bsvLocation.longitude = busStopsValue.Longitude

                    val distanceMeters = userCurrentLocation.distanceTo(bsvLocation)

                    if(distanceMeters <= Constant.USER_RADIUS) {
                        arrListOfNBBusStops.add(busStopsValue)
                    }
                }
                withContext(Main){
                    Log.i("BusStopsRepository","Entered BSR 4: ${arrListOfNBBusStops.count()}")
                    cJob?.complete()
                }
            }
        }


        return arrListOfNBBusStops
    }

    fun cancelJob(){
        cJob?.cancel()
    }
}