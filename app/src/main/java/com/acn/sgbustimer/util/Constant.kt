package com.acn.sgbustimer.util

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

        const val USER_RADIUS = 500.0 // 0.5KM
    }
}