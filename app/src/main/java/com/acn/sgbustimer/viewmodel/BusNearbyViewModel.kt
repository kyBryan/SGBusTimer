package com.acn.sgbustimer.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.repository.BusArrivalRepository
import com.acn.sgbustimer.repository.BusStopsRepository
import com.acn.sgbustimer.util.Constant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BusNearbyViewModel @Inject constructor(
    application: Application,
    @Named("BusStopsRepo") injStopsRepo: BusStopsRepository,
    @Named("AllBusStopsValue") injAllSGBusStops: ArrayList<BusStopsValue>
): AndroidViewModel(application) {

    // Application
    private val vmAppContext by lazy { getApplication<Application>().applicationContext }
    private val _permissionGranted = MutableLiveData<Boolean>()
    val permissionGranted: LiveData<Boolean>
        get() = _permissionGranted

    // Repository
    private val busArrivalRepo =  BusArrivalRepository()
    private val busStopsRepo = injStopsRepo

    // Data
    private val _arrListOfNearbyBusStopCodes = MutableLiveData<ArrayList<String>>()

    private val _listOfBusArrivalLiveData = Transformations
        .switchMap(_arrListOfNearbyBusStopCodes) { listOfBSC ->
            busArrivalRepo.getBusArrival(listOfBSC)
        }
    val listOfBusArrivalLiveData: LiveData<List<BusArrival>>
        get() = _listOfBusArrivalLiveData

    //private val arrListOfAllSGBusStops = busStopsRepo.getBusStopsValue()
    private var arrListOfAllSGBusStops: ArrayList<BusStopsValue> = injAllSGBusStops
    private val arrListOfNBBusStops by lazy { ArrayList<BusStopsValue>() }
    val listOfNBBusStops: List<BusStopsValue>
        get() = arrListOfNBBusStops

    
    // Google Map Location Objects
    private val _fusedLocationProviderClient = MutableLiveData<FusedLocationProviderClient>()
    val fusedLocationProviderClient: LiveData<FusedLocationProviderClient>
        get() = _fusedLocationProviderClient

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation



    init {
        _permissionGranted.value = true
        _fusedLocationProviderClient.value =
                LocationServices.getFusedLocationProviderClient(vmAppContext)
    }

    fun setListOfNearbyBusStop(arrListBusStopCodes: ArrayList<String>) {
        _arrListOfNearbyBusStopCodes.value = arrListBusStopCodes
    }

    /* Google Map Related */
    // Get User Current location
    fun userCurrentLocation(){

        if (ActivityCompat.checkSelfPermission(vmAppContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
             && ActivityCompat.checkSelfPermission(vmAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted
            _permissionGranted.value = false
            return
        }

        val task =  _fusedLocationProviderClient.value?.lastLocation
        task?.let { taskIT ->
            Timber.i("TaskIT Check")
            taskIT.addOnSuccessListener { location ->
                if (location != null) {
                    _currentLocation.value = location
                    //runBlocking {
                    //    busStopsRepo.cJob?.join()
                    updateNearbyBusStops() //}
                }
            }
        }
    }

    fun markerRadius(): CircleOptions {

        _currentLocation.value?.let {
            val circle: CircleOptions by lazy {
                CircleOptions()
                    .center(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
                    .radius(Constant.USER_RADIUS) // Meters
                    .strokeWidth(10f)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.argb(128, 255, 0, 0))
                    .clickable(true)
            }

            return circle
        }

        return CircleOptions()
    }



    private fun updateNearbyBusStops(){

        val unbsJob = Job()

        unbsJob.let { unbsJob ->
            CoroutineScope( Dispatchers.IO + unbsJob).launch {
                Timber.i("is cJob Active?: ${busStopsRepo.cJob?.isActive}")
                busStopsRepo.cJob?.join()
                _currentLocation.value?.let {
                    Timber.i("Updating Nearby Bus Stops...")

                    val tempArrListBusStopsCode = ArrayList<String>()
                    val bsvLocation = Location("")
                    if (arrListOfNBBusStops.count() != 0) arrListOfNBBusStops.clear()
                    for (busStopsValue in arrListOfAllSGBusStops) {
                        bsvLocation.latitude = busStopsValue.Latitude
                        bsvLocation.longitude = busStopsValue.Longitude

                        val distanceMeters = it.distanceTo(bsvLocation)

                        if (distanceMeters <= Constant.USER_RADIUS) {
                            Timber.i("Adding Bus Stop Code: ${busStopsValue.BusStopCode}")
                            arrListOfNBBusStops.add(busStopsValue)
                            tempArrListBusStopsCode.add(busStopsValue.BusStopCode)
                        }
                    }

                    withContext(Main){
                        _arrListOfNearbyBusStopCodes.value = tempArrListBusStopsCode
                        Timber.i("Updated Nearby Bus Stops found: ${_arrListOfNearbyBusStopCodes.value?.count()}")
                        unbsJob.complete()
                    }
                }
            }
        }
    }

}