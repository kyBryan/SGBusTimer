package com.acn.sgbustimer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.network.WebAccess
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class BusArrivalRepository {

    var cJob: CompletableJob? = null

    fun getBusArrival(listOfBusStopCodes: List<String>): LiveData<List<BusArrival>> {
        Log.i("BusArrivalRepository", "Entered getBusArrival() 1")
        cJob = Job()
        return object: LiveData<List<BusArrival>>(){
            override fun onActive() {
                super.onActive()
                Log.i("BusArrivalRepository", "Entered getBusArrival() 2")
                cJob?.let{ job ->
                    Log.i("BusArrivalRepository", "Entered getBusArrival() 3")
                    CoroutineScope(IO + job).launch {
                        Log.i("BusArrivalRepository", "Entered getBusArrival() 4")
                        val listOfBA = ArrayList<BusArrival>()

                        for (busStopCode in listOfBusStopCodes) {
                            val busArrival = WebAccess.busArrivalService.getBusArrivalApi(busStopCode)

                            busArrival.body()?.let {
                                listOfBA.add(it)
                            }

                        }
                        withContext(Main){
                            Log.i("BusArrivalRepository", "Entered getBusArrival() 5")
                            value = listOfBA.toList()
                            job.complete()
                        }
                    }
                }

            }
        }
    }


    fun cancelJobs() {
        cJob?.cancel()
    }

}