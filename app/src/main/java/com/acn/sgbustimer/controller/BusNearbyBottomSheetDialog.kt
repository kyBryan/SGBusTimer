package com.acn.sgbustimer.controller

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusNearbyBottomSheetDialogBinding
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.network.WebAccess
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class BusNearbyBottomSheetDialog : BottomSheetDialogFragment() {

    // Activity
    private lateinit var appActivity: Activity
    private lateinit var appContext: Context

    // Data
    private var listOfNearbyBusStops: MutableList<String> = mutableListOf("70211")

    // Recycle View
    private lateinit var busStopAdapter: BusNearbyBusStopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = DataBindingUtil.inflate<BusNearbyBottomSheetDialogBinding>(inflater, R.layout.bus_nearby_bottom_sheet_dialog, container, false)

        binding.rvBusStops.layoutManager = LinearLayoutManager(appContext)
        binding.rvBusStops.setHasFixedSize(true)

        busStopAdapter = BusNearbyBusStopAdapter(listOf()) { busStop: BusArrival -> busStopClicked(busStop)  }
        binding.rvBusStops.adapter = busStopAdapter

        Timber.i("EnteredBSD")

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

    /* BusArrival Api Related Starts */
    private fun loadNearbyBusAndUpdateList() {
        // Launch Kotlin Coroutine on Android's main thread
        // Note: better not to use GlobalScope, see:
        // https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html
        // An even better solution would be to use the Android livecycle-aware viewmodel
        // instead of attaching the scope to the activity.
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val webResponse = WebAccess.busArrivalApi.getBusArrivalAsync(listOfNearbyBusStops.get(0)).await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val busList: List<BusArrival>? = webResponse.body()
                    Timber.i(busList.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    busStopAdapter.busArrivalList = busList ?: listOf()
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
                    busStopAdapter.notifyDataSetChanged()
                } else {
                    // Print error information to the console
                    Timber.i("Error ${webResponse.code()}")
                }
            } catch (e: IOException) {
                // Error with network request
                Timber.i("Exception " + e.printStackTrace())
            }
        }
    }
    /* BusArrival Api Related Ends */

    private fun busStopClicked(busStop : BusArrival) {
        // Test code to add a new item to the list
        // Will be replaced with UI function soon
        //val newPart = PartData(Random.nextLong(0, 999999), "Infrared sensor")
        //addPart(newPart)
        //return

//        Toast.makeText(this, "Clicked: ${partItem.itemName}", Toast.LENGTH_LONG).show()
//
//        // Launch second activity, pass part ID as string parameter
//        val showDetailActivityIntent = Intent(this, PartDetailActivity::class.java)
//        //showDetailActivityIntent.putExtra(Intent.EXTRA_TEXT, partItem.id.toString())
//        showDetailActivityIntent.putExtra("ItemId", partItem.id)
//        showDetailActivityIntent.putExtra("ItemName", partItem.itemName)
//        startActivity(showDetailActivityIntent)
    }
}