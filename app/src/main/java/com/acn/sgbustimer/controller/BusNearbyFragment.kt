package com.acn.sgbustimer.controller

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusNearbyViewFragmentBinding
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.network.WebAccess
import com.acn.sgbustimer.util.Constant
import com.acn.sgbustimer.viewmodel.BusNearbyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber
import java.util.*


class BusNearbyFragment : Fragment(), OnMapReadyCallback {

    // Google Map
    private lateinit var mMap: GoogleMap

    // Current Location
    private val REQUEST_CODE = 101

    // Google Places
    private lateinit var placesClient: PlacesClient

    // Activity
    private lateinit var appActivity: Activity
    private lateinit var appContext: Context

    // Bottom Sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    // Recycle View
    private lateinit var busStopAdapter: BusNearbyBusStopAdapter

    // View Model
    private val busArrivalVM: BusNearbyViewModel by lazy {
        ViewModelProvider(this).get(BusNearbyViewModel::class.java)
    }

    // Data list of nearby bus stop codes
    private val arrListBusStopCodes =  ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<BusNearbyViewFragmentBinding>(inflater, R.layout.bus_nearby_view_fragment, container, false)

        setHasOptionsMenu(true)

        busArrivalVM.permissionGranted.observe(viewLifecycleOwner){ isPermissionGranted ->
            if(!isPermissionGranted){
                ActivityCompat.requestPermissions(appActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
        }

        busArrivalVM.currentLocation.observe(viewLifecycleOwner) {
            initMap()
        }

        // Map Start
        busArrivalVM.userCurrentLocation()

        // Google Place SDK, Testing
//        Places.initialize(appContext, getString(R.string.google_maps_key));
//        placesClient = Places.createClient(appContext)

        // Map End

        //Bottom Sheet Dialog Start
        bottomSheetBehavior = BottomSheetBehavior.from(binding.inclBusNearbyBottomSheetDialog.busNearbyBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // handle state changes
//                when (newState) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> Toast.makeText(appContext, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_EXPANDED -> Toast.makeText(appContext, "STATE_EXPANDED", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(appContext, "STATE_DRAGGING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(appContext, "STATE_SETTLING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(appContext, "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
//                    else -> Toast.makeText(appContext, "OTHER_STATE", Toast.LENGTH_SHORT).show()
//                }
            }
        })

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //Bottom Sheet Dialog Ends


        // Recycler View for listing bus stops
        binding.inclBusNearbyBottomSheetDialog.rvBusStops.layoutManager = LinearLayoutManager(appContext)
        binding.inclBusNearbyBottomSheetDialog.rvBusStops.setHasFixedSize(true)

        busStopAdapter = BusNearbyBusStopAdapter(listOf(), appContext) { busStop: BusArrival -> busStopClicked(busStop)  }
        binding.inclBusNearbyBottomSheetDialog.rvBusStops.adapter = busStopAdapter

        // Bus Arrival Api
        arrListBusStopCodes.addAll(listOf("70211", "70309", "66369"))

        busArrivalVM.listOfBusArrivalLiveData.observe(viewLifecycleOwner){ response ->
            if (response == null){
                Timber.i("Response is Null listOfBusArrivalLiveData")
                return@observe
            }
            Timber.i("Print Response Service No: ${response[0].services[0].serviceNo}")

            busStopAdapter.busArrivalList = response as List<BusArrival>
            // Inform recycler view that data has changed.
            // Makes sure the view re-renders itself
            busStopAdapter.notifyDataSetChanged()
        }

        busArrivalVM.setListOfNearbyBusStop(arrListBusStopCodes)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
        if (context is Activity) {
            Timber.i("Context is an instance of activity")
            appActivity = context

        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menuHome -> activity?.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    /* Google Map Related Start */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapPreSetup()

        // Use current location if location is turned on and permission valid
        if(busArrivalVM.currentLocation.value != null) {
            Timber.i("user latlong is ${busArrivalVM.currentLocation.value}")
            busArrivalVM.currentLocation.value?.let {
                val userlatLng = LatLng(it.latitude, it.longitude)
                val markerOptions = MarkerOptions().position(userlatLng).title("I Am Here!")
                mMap.animateCamera(CameraUpdateFactory.newLatLng(userlatLng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlatLng, 18f))
                mMap.addMarker(markerOptions)
            }
        }
        else {
            // else center to Singapore map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.MAP_BOUNDS.center, 11f))
        }
    }

    // Map Pre-setup, lock to singapore bounds
    private fun mapPreSetup(){
        mMap.setLatLngBoundsForCameraTarget(Constant.MAP_BOUNDS)
        mMap.setMinZoomPreference(11.0f)
        mMap.setMaxZoomPreference(20.0f)
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }



    /* Google Map Related Ends*/

    /* BusStop Item Clicked function Starts */
    private fun busStopClicked(busStop : BusArrival) {

        Timber.i("Clicked on BusStop: ${busStop.busStopCode}")
        val busStopCode = busStop.busStopCode
        var busServiceNoList: MutableList<String> = mutableListOf()
        var busServiceNextTime: MutableList<String> = mutableListOf()
        var busServiceNextTimeTwo: MutableList<String> = mutableListOf()

        for(i in 0 until busStop.services.count()){
            busServiceNoList.add(busStop.services[i].serviceNo)
            busServiceNextTime.add(busStop.services[i].nextBus.estimatedArrival)
            busServiceNextTimeTwo.add(busStop.services[i].nextBus2.estimatedArrival)
        }

        view?.let{
            it.findNavController().navigate(BusNearbyFragmentDirections.actionBusNearbyFragmentToBusTimeFragment(busStopCode, busServiceNoList.toTypedArray(), busServiceNextTime.toTypedArray(), busServiceNextTimeTwo.toTypedArray()))
        }
    }

    /* BusStop Item Clicked function Ends */


    // Permissioning
    private fun checkUserPermission(){
        // Location Permission Checks
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(appActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    busArrivalVM.userCurrentLocation()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}