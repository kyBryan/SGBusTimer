package com.acn.sgbustimer.viewmodel

import androidx.lifecycle.*
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.repository.BusArrivalRepository
import kotlinx.coroutines.launch

class BusNearbyViewModel(): ViewModel() {


    private val repository =  BusArrivalRepository()

    // Data
    private val _arrListOfNearbyBusStopCodes = MutableLiveData<ArrayList<String>>()
//    val arrListOfNearbyBusStopCodes: LiveData<ArrayList<String>>
//        get() = _arrListOfNearbyBusStopCodes


//    private val _listOfBusArrivalLiveData = MutableLiveData<List<BusArrival?>>()
//    val listOfBusArrivalLiveData: LiveData<List<BusArrival?>>
//        get() = _listOfBusArrivalLiveData

    private val _listOfBusArrivalLiveData = Transformations
        .switchMap(_arrListOfNearbyBusStopCodes) { listOfBSC ->
            repository.getBusArrival(listOfBSC)
        }
    val listOfBusArrivalLiveData: LiveData<List<BusArrival>>
        get() = _listOfBusArrivalLiveData



    init {

    }

//    fun fetchBusArrival(arrListBSC: ArrayList<String>): LiveData<List<BusArrival>> {
//        //val response = repository.getBusArrival(arrListBSC)
//
//        //return response
//    }

    fun setListOfNearbyBusStop(arrListBusStopCodes: ArrayList<String>) {
        _arrListOfNearbyBusStopCodes.value = arrListBusStopCodes
    }
}