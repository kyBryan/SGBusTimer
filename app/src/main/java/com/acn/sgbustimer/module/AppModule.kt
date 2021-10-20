package com.acn.sgbustimer.module

import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.repository.BusStopsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("BusStopsRepo")
    fun provideBusStopsRepo() = BusStopsRepository()

    @Singleton
    @Provides
    @Named("AllBusStopsValue")
    fun provideAllBusStopsValue(@Named("BusStopsRepo") busStopsRepo: BusStopsRepository): ArrayList<BusStopsValue> {
        return busStopsRepo.getBusStopsValue()
    }
}