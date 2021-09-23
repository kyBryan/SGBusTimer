package com.acn.sgbustimer.util

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Constant {
    companion object {
        val MAP_BOUNDS = LatLngBounds(
            LatLng(1.2024269951751805, 103.60315250626078),  // SW bounds
            LatLng(1.4711752956682944, 104.00449679832388) // NE bounds
        )
    }
}