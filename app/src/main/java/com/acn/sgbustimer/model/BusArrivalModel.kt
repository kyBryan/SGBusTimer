package com.acn.sgbustimer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusArrival (
    @Json(name="odata.metadata")
    val odataMetadata: String,
    val BusStopCode: String,
    val Services: List<Service>
)

data class Service (
    val ServiceNo: String,
    val Operator: Operator,
    val NextBus: NextBus,
    val NextBus2: NextBus,
    val NextBus3: NextBus
)

data class NextBus (
    val OriginCode: String,
    val DestinationCode: String,
    val EstimatedArrival: String,
    val Latitude: String,
    val Longitude: String,
    val VisitNumber: String,
    val Load: Load,
    val Feature: Feature,
    val Type: Type
)

enum class Feature {
    Empty,
    Wab
}

enum class Load {
    Empty,
    Sea
}

enum class Type {
    DD,
    Empty,
    SD
}

enum class Operator {
    Sbst,
    Smrt,
    TTS
}