package com.acn.sgbustimer.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.acn.sgbustimer.di.module.AppModule
import com.acn.sgbustimer.di.module.BusArrivalModule
import com.acn.sgbustimer.di.module.BusStopsModule
import com.acn.sgbustimer.di.module.CommonObjectModule
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsSection
import com.acn.sgbustimer.model.BusStopsValue
import com.acn.sgbustimer.repository.BusArrivalRepository
import com.acn.sgbustimer.util.Constant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BusNearbyViewModel @Inject constructor(
    application: Application,
    @Named(BusStopsModule.ALL_BUS_STOPS_VALUE) injAllSGBusStops: ArrayList<BusStopsValue>,
    @Named(CommonObjectModule.COMMON_REST_ADAPTER) injRetrofit: Retrofit,
    @Named(BusStopsModule.NEARBY_BUS_STOPS_LIST) injNBBusStopList: ArrayList<BusStopsValue>
) : AndroidViewModel(application) {

    // Application
    private val vmAppContext by lazy { getApplication<Application>().applicationContext }
    private val _permissionGranted = MutableLiveData<Boolean>()
    val permissionGranted: LiveData<Boolean>
        get() = _permissionGranted

    // Retrofit
    private val retrofit = injRetrofit

    // Data
    private val _arrListOfNearbyBusStopCodes = MutableLiveData<ArrayList<String>>()

    private val _listOfBusArrivalLiveData = MutableLiveData<List<BusArrival>>()
    val listOfBusArrivalLiveData: LiveData<List<BusArrival>>
        get() = _listOfBusArrivalLiveData

    private var arrListOfAllSGBusStops: ArrayList<BusStopsValue> = injAllSGBusStops
    private val arrListOfNBBusStops by lazy { injNBBusStopList }
    val listOfNBBusStops: List<BusStopsValue>
        get() = arrListOfNBBusStops

    val arrListOfBusStopsSection by lazy { ArrayList<BusStopsSection>() }

    // Google Map Location Objects
    private val _fusedLocationProviderClient = MutableLiveData<FusedLocationProviderClient>()
    val fusedLocationProviderClient: LiveData<FusedLocationProviderClient>
        get() = _fusedLocationProviderClient

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location>
        get() = _currentLocation

    // Initializer
    init {
        _permissionGranted.value = true
        _fusedLocationProviderClient.value =
            LocationServices.getFusedLocationProviderClient(vmAppContext)
    }

    fun setListOfNearbyBusStop(arrListBusStopCodes: ArrayList<String>) {
        _arrListOfNearbyBusStopCodes.value = arrListBusStopCodes
    }

    /* Google Map Related */
    fun userCurrentLocation() {
        // Get User Current location
        if (ActivityCompat.checkSelfPermission(vmAppContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(vmAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted
            _permissionGranted.value = false
            return
        }

        val task = _fusedLocationProviderClient.value?.lastLocation
        task?.let { taskIT ->
            Timber.i("TaskIT Check")
            taskIT.addOnSuccessListener { location ->
                if (location != null) {
                    _currentLocation.value = location
                    // runBlocking {
                    //    busStopsRepo.cJob?.join()
                    updateNearbyBusStops() // }
                }
            }
        }
    }

    fun markerRadius(): CircleOptions {
        // To create a geofence radius based on user current location; to be called from the UI
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
    /* Google Map Related ENDS */

    /* Data Logic */
    private fun updateNearbyBusStops() {
       // To Update all the nearby bus stop based on user current location
       viewModelScope.launch {
            _currentLocation.value?.let {
                Timber.i("Updating Nearby Bus Stops...")
                BusStopsModule.job.join()
                val tempArrListBusStopsCode = ArrayList<String>()
                val bsvLocation = Location("")
                if (arrListOfNBBusStops.count() != 0) {
                    arrListOfNBBusStops.clear()
                    BusStopsModule.arrListOfNBBusStops.clear()
                }

                for (busStopsValue in arrListOfAllSGBusStops) {
                    bsvLocation.latitude = busStopsValue.Latitude
                    bsvLocation.longitude = busStopsValue.Longitude

                    val distanceMeters = it.distanceTo(bsvLocation)

                    if (distanceMeters <= Constant.USER_RADIUS) {
                        Timber.i("Adding Bus Stop Code: ${busStopsValue.BusStopCode}")
                        BusStopsModule.arrListOfNBBusStops.add(busStopsValue)
                        //tempArrListBusStopsCode.add(busStopsValue.BusStopCode)
                    }
                }

                arrListOfNBBusStops.addAll(BusStopsModule.arrListOfNBBusStops)

                withContext(Main) {
                    _listOfBusArrivalLiveData.value = BusArrivalModule.provideNearbyBusArrivalList(retrofit).toList()
                    Timber.i("Updated Nearby Bus Stops found: ${_listOfBusArrivalLiveData.value?.count()}")
                }
            }
        }
    }

    fun combineListForAdapter(){
        val countID = 0
        for (busStop in arrListOfNBBusStops) {
            _listOfBusArrivalLiveData.value?.let{
                val busStopsSection = BusStopsSection(countID, busStop, it[countID].services)
                arrListOfBusStopsSection.add(busStopsSection)
            }
        }

    }

    /* Data Logic Ends */
}
