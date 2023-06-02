package com.letsride.models

import kotlinx.serialization.Serializable
import kotlin.math.*

@Serializable
data class BikeStation(
    val name: String,
    val address: String,
    val capacity: Int,
    val vacantSlots: Int,
    val availableBikes: Int,
    val position: Position,
)

@Serializable
data class Position(
    val longitude: Double,
    val latitude: Double,
) {
    /**
     * Calculate distance between to [other]
     */
    fun distanceTo(other: Position): Double {
        val radius = 6378137.0
        val deltaLat = other.latitude - latitude
        val deltaLon = other.longitude - longitude
        val angle = 2 * asin(
            sqrt(
                sin(deltaLat / 2).pow(2.0) + cos(latitude) * cos(other.latitude) * sin(deltaLon / 2).pow(2.0),
            ),
        )
        return radius * angle
    }
}
