package com.acn.sgbustimer.util

import android.content.res.Resources
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Constant {
    companion object {
        val MAP_BOUNDS = LatLngBounds(
            LatLng(1.2024269951751805, 103.60315250626078),  // SW bounds
            LatLng(1.4711752956682944, 104.00449679832388) // NE bounds
        )

        const val DATAMALL_API_KEY = "r6LVWluBQXC0hv3Hjc3OOg=="

        const val DATAMALL_API = "http://datamall2.mytransport.sg/ltaodataservice/"

        const val USER_RADIUS = 500.0 // 1KM is 1000.0

        val Int.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

        val Float.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }
}