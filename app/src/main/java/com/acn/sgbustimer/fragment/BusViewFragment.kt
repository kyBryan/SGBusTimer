package com.acn.sgbustimer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusViewFragmentBinding

class BusViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<BusViewFragmentBinding>(inflater, R.layout.bus_view_fragment, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }
}
