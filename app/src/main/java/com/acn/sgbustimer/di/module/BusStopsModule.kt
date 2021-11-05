package com.acn.sgbustimer.di.module

import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.service.DataMallService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BusStopsModule {

    lateinit var job: Job

    @Singleton
    @Provides
    @Named("AllBusStopsValue")
    fun provideAllBusStopsValue(@Named(CommonObjectModule.COMMON_REST_ADAPTER) retrofit: Retrofit): ArrayList<BusStopsValue> {

        Timber.i("fetching all BusStopsValue...")
        val dataMallService = retrofit.create(DataMallService::class.java)
        val arrListOfAllBusStopsValue = ArrayList<BusStopsValue>()
        val skipStringList = listOf("0", "500", "1000", "1500", "2000", "2500", "3000", "3500", "4000", "4500", "5000")

        job = CoroutineScope(Dispatchers.IO).launch {
            for (skipStr in skipStringList) {
                Timber.i("Processing Bus Stops Value at skipstr: $skipStr")
                val busStops = dataMallService.getBusStopsApi(skipStr)

                busStops.body()?.let {
                    arrListOfAllBusStopsValue.addAll(it.value)
                }
            }
        }

        return arrListOfAllBusStopsValue
    }
}