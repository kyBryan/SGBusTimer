package com.acn.sgbustimer.model

import com.squareup.moshi.Json

data class BusStops(
    @Json(name = "odata.metadata")
    val odataMetadata: String,
    val value: List<BusStopsValue>
)

data class BusStopsValue(
    val BusStopCode: String,
    val RoadName: String,
    val Description: String,
    val Latitude: Double,
    val Longitude: Double
)

data class BusStopsSection(
    val type: Int,
    val busStopValue: BusStopsValue,
    val busServiceList: List<Service>
)

