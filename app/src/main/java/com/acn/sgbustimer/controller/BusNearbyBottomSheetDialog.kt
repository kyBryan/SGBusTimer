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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<BusNearbyBottomSheetDialogBinding>(inflater, R.layout.bus_nearby_bottom_sheet_dialog, container, false)


        return binding.root
    }

}