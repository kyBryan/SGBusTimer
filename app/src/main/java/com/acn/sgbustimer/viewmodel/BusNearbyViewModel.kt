package com.acn.sgbustimer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.repository.BusNearbyRepository
import kotlinx.coroutines.launch

class BusNearbyViewModel: ViewModel() {


    private val repository =  BusNearbyRepository()

    private val _listOfBusArrivalLiveData = MutableLiveData<List<BusArrival?>>()
    val listOfBusArrivalLiveData: LiveData<List<BusArrival?>>
        get() = _listOfBusArrivalLiveData


    init {

    }

    fun refreshBusArrival(listOfBusStopCodes: List<String>){
        viewModelScope.launch {
            val listOfResponse: MutableList<BusArrival?> = mutableListOf()
            for (busStopCode in listOfBusStopCodes) {
                val response = repository.getBusArrivalAsync(busStopCode)

                listOfResponse.add(response)
            }

            _listOfBusArrivalLiveData.postValue(listOfResponse)
        }
    }
}