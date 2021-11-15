package com.acn.sgbustimer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusNearbyBusStopListBinding
import com.acn.sgbustimer.model.BusArrival
import com.acn.sgbustimer.model.BusStopsSection
import com.acn.sgbustimer.model.BusStopsValue
import timber.log.Timber

class BusNearbyBusStopAdapter(
    private val clickListener: (BusStopsSection) -> Unit
): ListAdapter<BusStopsSection, BusNearbyBusStopAdapter.BaseViewHolder>(
    BusNearbyBusStopDiffUtil()
) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        Timber.i("Create view holder for $viewType")

        val inflater = LayoutInflater.from(parent.context)
        return BusNearbyBusStopViewHolder(DataBindingUtil.inflate(inflater, R.layout.bus_nearby_bus_stop_list, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Timber.i("Binding viewHolder $holder with position=$position")
        val item = getItem(position)
        //val clickListener: (BusStopsSection) -> Unit
        holder.bindData(item, clickListener)
    }

//    override fun submitList(list: MutableList<BusStopsSection>?) {
//        super.submitList(list?.map { it.copy()})
//    }

    /* BaseViewHolder */
    abstract inner class BaseViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        abstract fun bindData(item: BusStopsSection, clickListener: (BusStopsSection) -> Unit)
    }

    /* ViewHolder */
    inner class BusNearbyBusStopViewHolder(
        val binding: BusNearbyBusStopListBinding
    ): BaseViewHolder(binding.root){
        override fun bindData(item: BusStopsSection, clickListener: (BusStopsSection) -> Unit) {
            Timber.i("List Check: ${item.busStopValue.BusStopCode}")
            binding.tvBusStopName.text = item.busStopValue.Description
            binding.tvBusStopCode.text = item.busStopValue.BusStopCode

            val busServiceCount = item.busServiceList.count()

            if (busServiceCount == 0) {
                // Set the text and update the constraint for moreBus textview
                val moreBusTV = binding.tvMoreBus
                moreBusTV.text = itemView.context.getString(R.string.no_available_bus)
                val lParams = moreBusTV.layoutParams as ConstraintLayout.LayoutParams
                lParams.startToStart = binding.itemGroupCL.id

                busServiceVisibility(binding, busServiceCount, true)
            } else {
                // There are Bus Service Found
                val busServiceCount = item.busServiceList.count()
                val moreBusCount = busServiceCount - 5

                if (busServiceCount >= 5) {
                    // There are 5 or more bus service found
                    val busOrBuses = if (moreBusCount != 1) "es" else ""
                    for (i in 0..4){
                        binding.tvBusNo5.text = item.busServiceList[i].serviceNo
                    }
                    binding.tvMoreBus.text = ("+$moreBusCount ${itemView.context.getString(R.string.x_more_bus)}$busOrBuses")

                    val isMoreBus = (moreBusCount != 0)
                    busServiceVisibility(binding, busServiceCount, isMoreBus)
                } else {
                    // 4 or less buses
                    val listOfTVBusNo = listOf(binding.tvBusNo1, binding.tvBusNo2, binding.tvBusNo3, binding.tvBusNo4, binding.tvBusNo5)

                    for (i in 0 until busServiceCount - 1) {
                        listOfTVBusNo[i].text = item.busServiceList[i].serviceNo
                    }

                    busServiceVisibility(binding, busServiceCount, false)
                }
            }

            binding.root.setOnClickListener { clickListener(item) }
        }

    }

    /* DiffUtil */
    class BusNearbyBusStopDiffUtil : DiffUtil.ItemCallback<BusStopsSection>() {

        override fun areItemsTheSame(oldItem: BusStopsSection, newItem: BusStopsSection): Boolean {
            Timber.i("Enter areItemsTheSame: ${oldItem.javaClass == newItem.javaClass}")
            // Identify items as a whole.
            return oldItem.javaClass == newItem.javaClass
        }

        override fun areContentsTheSame(oldItem: BusStopsSection, newItem: BusStopsSection): Boolean {
            Timber.i("Enter areContentsTheSame: ${oldItem.busStopValue.BusStopCode == newItem.busStopValue.BusStopCode}")
            // Can use a unique identifier like BusStopCode, to check if items are the same.
            // If just using oldItem == newItem, their memory address might be different, therefore result in a crash.
            return oldItem.busStopValue.BusStopCode == newItem.busStopValue.BusStopCode
        }
    }

    /* Data Logic */
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

