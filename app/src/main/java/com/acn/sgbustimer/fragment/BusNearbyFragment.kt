package com.acn.sgbustimer.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.acn.sgbustimer.adapter.BusNearbyBusStopAdapter
import com.acn.sgbustimer.di.module.AppModule
import com.acn.sgbustimer.di.module.BusStopsModule
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsSection
import com.acn.sgbustimer.repository.BusArrivalRepository
import com.acn.sgbustimer.util.Constant
import com.acn.sgbustimer.util.Constant.Companion.dp
import com.acn.sgbustimer.viewmodel.BusNearbyViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class BusNearbyFragment : Fragment() {

    // Google Map
    private var mapJob: CompletableJob? = null
    private lateinit var mMap: GoogleMap

    // Current Location
    private val REQUEST_CODE = 101

    // Activity
    private lateinit var appActivity: Activity
    private lateinit var appContext: Context

    // Bottom Sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    // List Adapter
    private val busStopAdapter by lazy { BusNearbyBusStopAdapter(){ busStopsSection: BusStopsSection -> busStopClicked(busStopsSection)  } }

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

        // Set Google Map
        initMap()

        busArrivalVM.permissionGranted.observe(viewLifecycleOwner){ isPermissionGranted ->
            if(!isPermissionGranted){
                ActivityCompat.requestPermissions(appActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
        }

        busArrivalVM.currentLocation.observe(viewLifecycleOwner) {
            userCurrentLocMarker(busArrivalVM.markerRadius())
        }

        // Map Start
        busArrivalVM.userCurrentLocation()

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
                when (newState) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> Toast.makeText(appContext, "STATE_COLLAPSED", Toast.LENGTH_SHORT).show()
                    BottomSheetBehavior.STATE_EXPANDED -> binding.inclBusNearbyBottomSheetDialog.busContentContainer.setPadding(0,0,0, 0.dp)
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> binding.inclBusNearbyBottomSheetDialog.busContentContainer.setPadding(0,0,0, 300.dp)
//                    BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(appContext, "STATE_DRAGGING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(appContext, "STATE_SETTLING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(appContext, "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
//                    else -> Toast.makeText(appContext, "OTHER_STATE", Toast.LENGTH_SHORT).show()
                }
            }
        })
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        //Bottom Sheet Dialog Ends


        // Recycler View for listing bus stops
        binding.inclBusNearbyBottomSheetDialog.rvBusStops.layoutManager = LinearLayoutManager(appContext)
        //binding.inclBusNearbyBottomSheetDialog.rvBusStops.setHasFixedSize(true)

        binding.inclBusNearbyBottomSheetDialog.rvBusStops.adapter = busStopAdapter

        busArrivalVM.listOfBusArrivalLiveData.observe(viewLifecycleOwner){ response ->
            if (response == null){
                Timber.i("Response is Null listOfBusArrivalLiveData")
                return@observe
            }

            Timber.i("Observed Variable changed.")
            busArrivalVM.combineListForAdapter()
            busStopAdapter.submitList(busArrivalVM.arrListOfBusStopsSection.toMutableList())

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        mMap.clear()
        BusArrivalRepository().cancelJobs()
        mapJob?.cancel()
        BusStopsModule.job.cancel()
    }

    /* Google Map Related Start */

    // Map Pre-setup, lock to singapore bounds
    private fun mapPreSetup(){
        mMap.setLatLngBoundsForCameraTarget(Constant.MAP_BOUNDS)
        mMap.setMinZoomPreference(11.0f)
        mMap.setMaxZoomPreference(20.0f)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.MAP_BOUNDS.center, 11f))
    }

    private fun initMap() {
        mapJob = Job()

        mapJob?.let { mapJob ->
            CoroutineScope(Dispatchers.IO + mapJob).launch {
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

                withContext(Main){
                    mapFragment.getMapAsync {
                        // *Overriding OnMapReady is here
                        Timber.i("Initializing Map...")
                        mMap = it
                        mapPreSetup()
                        Timber.i("Initialized Map completed.")
                    }

                    mapJob.complete()
                }
            }
        }
    }

    private fun userCurrentLocMarker(bufferRadius: CircleOptions){
        val uclJob = Job()

        uclJob.let { uJob ->
            CoroutineScope(Dispatchers.IO + uJob).launch {
                mapJob?.join()
                // Use current location if location is turned on and permission valid
                if (busArrivalVM.currentLocation.value != null) {
                    Timber.i("user latlong is ${busArrivalVM.currentLocation.value}")
                    busArrivalVM.currentLocation.value?.let {
                        val userlatLng = LatLng(it.latitude, it.longitude)
                        val markerOptions = MarkerOptions().position(userlatLng).title("I Am Here!")
                        withContext(Main){
                            //mMap.animateCamera(CameraUpdateFactory.newLatLng(userlatLng))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlatLng, 15f))
                            mMap.addMarker(markerOptions)
                            mMap.addCircle(bufferRadius)

                            uJob.complete()
                        }
                    }
                } else {
                    withContext(Main){
                        // else center to Singapore map
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.MAP_BOUNDS.center, 11f))

                        uJob.complete()
                    }
                }
            }
        }
    }



    /* Google Map Related Ends*/

    /* BusStop Item Clicked function Starts */
    private fun busStopClicked(busStop : BusStopsSection) {

        Timber.i("Clicked on BusStop: ${busStop.busStopValue.BusStopCode}")
        val busStopCode = busStop.busStopValue.BusStopCode
        val busServiceNoList: MutableList<String> = mutableListOf()
        val busServiceNextTime: MutableList<String> = mutableListOf()
        val busServiceNextTimeTwo: MutableList<String> = mutableListOf()

        for(i in 0 until busStop.busServiceList.count()){
            busServiceNoList.add(busStop.busServiceList[i].serviceNo)
            busServiceNextTime.add(busStop.busServiceList[i].nextBus.estimatedArrival)
            busServiceNextTimeTwo.add(busStop.busServiceList[i].nextBus2.estimatedArrival)
        }

        view?.let{
            it.findNavController().navigate(
                BusNearbyFragmentDirections.actionBusNearbyFragmentToBusTimeFragment(
                    busStopCode,
                    busServiceNoList.toTypedArray(),
                    busServiceNextTime.toTypedArray(),
                    busServiceNextTimeTwo.toTypedArray()
                )
            )
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