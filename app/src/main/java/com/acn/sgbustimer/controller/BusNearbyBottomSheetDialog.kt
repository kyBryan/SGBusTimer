package com.acn.sgbustimer.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.acn.sgbustimer.R
import com.acn.sgbustimer.databinding.BusNearbyBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BusNearbyBottomSheetDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding = DataBindingUtil.inflate<BusNearbyBottomSheetDialogBinding>(inflater, R.layout.bus_nearby_bottom_sheet_dialog, container, false)


        return binding.root
    }
}