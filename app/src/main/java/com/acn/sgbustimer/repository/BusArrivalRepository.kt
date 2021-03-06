package com.acn.sgbustimer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.service.WebAccess
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BusArrivalRepository {

    var cJob: CompletableJob? = null

    fun getBusArrival(listOfBusStopCodes: List<String>): LiveData<List<BusArrival>> {
        Timber.i("Entered getBusArrival() 1")
        cJob = Job()
        return object : LiveData<List<BusArrival>>() {
            override fun onActive() {
                super.onActive()
                Timber.i("Entered getBusArrival() 2")
                cJob?.let { job ->
                    Timber.i("Entered getBusArrival() 3")
                    CoroutineScope(IO + job).launch {
                        Timber.i("Entered getBusArrival() 4")
                        val listOfBA = ArrayList<BusArrival>()

                        for (busStopCode in listOfBusStopCodes) {
                            val busArrival = WebAccess.dataMallService.getBusArrivalApi(busStopCode)

                            busArrival.body()?.let {
                                listOfBA.add(it)
                            }
                        }
                        withContext(Main) {
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
