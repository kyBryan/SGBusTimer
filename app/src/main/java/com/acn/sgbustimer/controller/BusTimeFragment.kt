package com.acn.sgbustimer.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusTimeFragmentBinding

class BusTimeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<BusTimeFragmentBinding>(inflater, R.layout.bus_time_fragment, container, false)

        arguments?.let {
            val args = BusTimeFragmentArgs.fromBundle(it)

            binding.tvTemp.text = args.argsBusStopServiceNo?.get(0)
        }

        // Inflate the layout for this fragment
        return binding.root
    }
}
