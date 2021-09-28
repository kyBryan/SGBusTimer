package com.acn.sgbustimer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusArrival (
    @field:Json(name="odata.metadata")
    val odataMetadata: String,
    @field:Json(name="BusStopCode")
    val busStopCode: String,
    @field:Json(name="Services")
    val services: List<Service>
)

@JsonClass(generateAdapter = true)
data class Service (
    @field:Json(name="ServiceNo")
    val serviceNo: String,
    @field:Json(name="Operator")
    val operator: String,
    @field:Json(name="NextBus")
    val nextBus: NextBus,
    @field:Json(name="NextBus2")
    val nextBus2: NextBus,
    @field:Json(name="NextBus3")
    val nextBus3: NextBus
)

@JsonClass(generateAdapter = true)
data class NextBus (
    @field:Json(name="OriginCode")
    val originCode: String,
    @field:Json(name="DestinationCode")
    val destinationCode: String,
    @field:Json(name="EstimatedArrival")
    val estimatedArrival: String,
    @field:Json(name="Latitude")
    val latitude: String,
    @field:Json(name="Longitude")
    val longitude: String,
    @field:Json(name="VisitNumber")
    val visitNumber: String,
    @field:Json(name="Load")
    val load: String,
    @field:Json(name="Feature")
    val feature: String,
    @field:Json(name="Type")
    val type: String
)

//enum class Feature {
//    Empty,
//    Wab
//}
//
//enum class Load {
//    Empty,
//    Sea
//}
//
//enum class Type {
//    DD,
//    Empty,
//    SD
//}
//
//enum class Operator {
//    Sbst,
//    Smrt,
//    TTS
//}