package com.letsride.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BikeStation(
    val stationId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val vacantSlots: Int,
    val availableBikes: Int,
)


@Serializable
data class Station(
    @SerialName("station_id")
    val stationId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int
)