package com.acn.sgbustimer.model

data class BusArrival (
    val odataMetadata: String,
    val busStopCode: String,
    val services: List<Service>
)

data class Service (
    val serviceNo: String,
    val operator: Operator,
    val nextBus: NextBus,
    val nextBus2: NextBus,
    val nextBus3: NextBus
)

data class NextBus (
    val originCode: String,
    val destinationCode: String,
    val estimatedArrival: String,
    val latitude: String,
    val longitude: String,
    val visitNumber: String,
    val load: Load,
    val feature: Feature,
    val type: Type
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