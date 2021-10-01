package com.acn.sgbustimer.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusTimeFragmentBinding
import com.acn.sgbustimer.model.BusArrival


class BusTimeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<BusTimeFragmentBinding>(inflater, R.layout.bus_time_fragment, container, false)

//        var args = BusTimeFragmentArgs.fromBundle(requireArguments())
//
//        binding.tvTemp.text = args.mBusArrival.services[0].serviceNo


        // Inflate the layout for this fragment
        return binding.root
    }

}