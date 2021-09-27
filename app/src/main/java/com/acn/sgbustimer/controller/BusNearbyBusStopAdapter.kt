package com.acn.sgbustimer.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.databinding.BusNearbyBusStopListBinding
import com.acn.sgbustimer.model.BusArrival

class BusNearbyBusStopAdapter(var busArrivalList: List<BusArrival>, private val clickListener: (BusArrival) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BusNearbyBusStopListBinding.inflate(inflater, parent, false)

        return BusStopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BusStopViewHolder).bind(busArrivalList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return busArrivalList.count()
    }

    inner class BusStopViewHolder(private val binding: BusNearbyBusStopListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(busArrival: BusArrival, clickListener: (BusArrival) -> Unit) {
            binding.tvBusStopCode.text = busArrival.BusStopCode
            binding.root.setOnClickListener { clickListener(busArrival) }
        }
    }

}