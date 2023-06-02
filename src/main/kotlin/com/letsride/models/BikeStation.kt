package com.letsride.models

import kotlinx.serialization.Serializable

@Serializable
data class BikeStation(
    val name: String,
    val address: String,
    val capacity: Int,
    val vacantSlots: Int,
    val availableBikes: Int,
)
