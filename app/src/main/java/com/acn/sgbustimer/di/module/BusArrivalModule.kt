package com.acn.sgbustimer.di.module

import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.service.DataMallService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BusArrivalModule {

    @Provides
    @Singleton
    @Named(NEARBY_BUS_ARRIVAL_LIST)
    suspend fun provideNearbyBusArrivalList(@Named(CommonObjectModule.COMMON_REST_ADAPTER) retrofit: Retrofit): ArrayList<BusArrival>{
        val arrListOfBusArrival = ArrayList<BusArrival>()
        val dataMallService = retrofit.create(DataMallService::class.java)

        BusStopsModule.arrListOfNBBusStops.let { listOfBusStopValue ->
            CoroutineScope(Dispatchers.IO).async {

                for (busStopValue in listOfBusStopValue){
                    val result = dataMallService.getBusArrivalApi(busStopValue.BusStopCode)

                    result.body()?.let{
                        arrListOfBusArrival.add(it)
                    }
                }

                Timber.i("Check list count: ${arrListOfBusArrival.count()}")

            }
        }.await()

        Timber.i("Check list count: ${arrListOfBusArrival.count()}")

        return arrListOfBusArrival
    }


    const val NEARBY_BUS_ARRIVAL_LIST = "NEARBY_BUS_ARRIVAL_LIST"
}