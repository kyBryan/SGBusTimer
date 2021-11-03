package com.acn.sgbustimer.repository

import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.network.WebAccess
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.Dispatchers.Main
import timber.log.Timber

class BusStopsRepository {
    var cJob: CompletableJob? = null

    fun getBusStopsValue(): ArrayList<BusStopsValue> {
        val arrListOfNBBusStops = ArrayList<BusStopsValue>()

        cJob = Job()

        cJob?.let { job ->
            CoroutineScope(Dispatchers.IO + job).launch {
                Timber.i("BusStopsRepository is being called.")

                val skipStringList = listOf("0", "500", "1000", "1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000")

                for (skipStr in skipStringList) {
                    val busStops = WebAccess.dataMallService.getBusStopsApi(skipStr)

                    busStops.body()?.let {
                        arrListOfNBBusStops.addAll(it.value)
                    }
                }

                withContext(Main) {
                    Timber.i("BusStopsRepository is completed, fetch count: ${arrListOfNBBusStops.count()}.")
                    cJob?.complete()
                }
            }
        }

        return arrListOfNBBusStops
    }

    fun cancelJob() {
        cJob?.cancel()
    }
}
