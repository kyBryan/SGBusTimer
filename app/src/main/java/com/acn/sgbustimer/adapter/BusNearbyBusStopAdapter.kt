package com.acn.sgbustimer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusNearbyBusStopListBinding
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsValue

class BusNearbyBusStopAdapter(var busArrivalList: List<BusArrival>, var busStopsList: List<BusStopsValue>, val appContext: Context, private val clickListener: (BusArrival) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BusNearbyBusStopListBinding.inflate(inflater, parent, false)

        return BusStopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BusStopViewHolder).bind(busArrivalList[position], busStopsList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return busArrivalList.count()
    }

    inner class BusStopViewHolder(private val binding: BusNearbyBusStopListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(busArrival: BusArrival, busStopsValue: BusStopsValue, clickListener: (BusArrival) -> Unit) {
            binding.tvBusStopName.text = busStopsValue.Description
            binding.tvBusStopCode.text = busArrival.busStopCode

            val busServiceCount = busArrival.services.count()

            if (busServiceCount == 0) {
                // Set the text and update the constraint for moreBus textview
                val moreBusTV = binding.tvMoreBus
                moreBusTV.text = appContext.getString(R.string.no_available_bus)
                val lParams = moreBusTV.layoutParams as ConstraintLayout.LayoutParams
                lParams.startToStart = binding.itemGroupCL.id

                busServiceVisibility(binding, busServiceCount, true)
            } else {
                // There are Bus Service Found
                val busServiceCount = busArrival.services.count()
                val moreBusCount = busServiceCount - 5

                if (busServiceCount >= 5) {
                    // There are 5 or more bus service found
                    val busOrBuses = if (moreBusCount != 1) "es" else ""
                    binding.tvBusNo1.text = busArrival.services[0].serviceNo
                    binding.tvBusNo2.text = busArrival.services[1].serviceNo
                    binding.tvBusNo3.text = busArrival.services[2].serviceNo
                    binding.tvBusNo4.text = busArrival.services[3].serviceNo
                    binding.tvBusNo5.text = busArrival.services[4].serviceNo
                    binding.tvMoreBus.text = ("+$moreBusCount ${appContext.getString(R.string.x_more_bus)}$busOrBuses")

                    val isMoreBus = (moreBusCount != 0)
                    busServiceVisibility(binding, busServiceCount, isMoreBus)
                } else {
                    // 4 or less buses
                    val listOfTVBusNo = listOf(binding.tvBusNo1, binding.tvBusNo2, binding.tvBusNo3, binding.tvBusNo4, binding.tvBusNo5)

                    for (i in 0 until busServiceCount - 1) {
                        listOfTVBusNo[i].text = busArrival.services[i].serviceNo
                    }

                    busServiceVisibility(binding, busServiceCount, false)
                }
            }

            binding.root.setOnClickListener { clickListener(busArrival) }
        }
    }

    private fun busServiceVisibility(binding: BusNearbyBusStopListBinding, countBusService: Int, moreBus: Boolean) {
        // 0 = just show moreBus without loop
        if (countBusService == 0) {
            binding.tvMoreBus.visibility = View.VISIBLE
        } else {
            // 1 or more buses
            val listOfTVBusNo = listOf(binding.tvBusNo1, binding.tvBusNo2, binding.tvBusNo3, binding.tvBusNo4, binding.tvBusNo5)

            if (countBusService >= 5) {
                // 5 or more Buses
                listOfTVBusNo[0].visibility = View.VISIBLE
                listOfTVBusNo[1].visibility = View.VISIBLE
                listOfTVBusNo[2].visibility = View.VISIBLE
                listOfTVBusNo[3].visibility = View.VISIBLE
                listOfTVBusNo[4].visibility = View.VISIBLE
            } else {
                // 4 or less buses
                for (i in 0 until countBusService - 1) {
                    listOfTVBusNo[i].visibility = View.VISIBLE
                }
            }

            binding.tvMoreBus.visibility = if (moreBus) View.VISIBLE else View.GONE
        }
    }
}
