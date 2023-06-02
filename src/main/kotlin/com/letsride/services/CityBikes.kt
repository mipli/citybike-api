package com.letsride.services

import com.letsride.models.BikeStation
import com.letsride.models.Position
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Service interface for fetching and dealing with data from a City Bike API
 */
interface CityBikeService {
    suspend fun getStationList(): List<BikeStation>
}

/**
 * Services for fetching and dealing with data from the Oslo City Bike API
 */
class CityBikeServiceImpl(private val clientIdentifier: String, engine: HttpClientEngine) : CityBikeService {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    private suspend fun getOsloStationInformation(): List<StationInformation> {
        val response: Response<StationInformation> = client.get("https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
                append("Client-Identifier", clientIdentifier)
            }
        }.body()

        return response.data.stations
    }

    private suspend fun getOsloStationStatus(): List<StationStatus> {
        val response: Response<StationStatus> = client.get("https://gbfs.urbansharing.com/oslobysykkel.no/station_status.json") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
                append("Client-Identifier", clientIdentifier)
            }
        }.body()

        return response.data.stations
    }

    private fun convertToBikeStations(infoList: List<StationInformation>, statusList: List<StationStatus>): List<BikeStation> {
        return infoList.mapNotNull { info ->
            statusList.find { it.stationId == info.stationId }?.let {
                Pair(info, it)
            }
        }.map {
            val info = it.first
            val status = it.second
            BikeStation(
                name = info.name,
                address = info.address,
                capacity = info.capacity,
                vacantSlots = status.numDocksAvailable,
                availableBikes = status.numBikesAvailable,
                position = Position(info.lon, info.lat),
            )
        }
    }

    /**
     * Fetch a list of all available stations for Oslo City Bikes
     */
    override suspend fun getStationList(): List<BikeStation> {
        val infoList = getOsloStationInformation()
        val statusList = getOsloStationStatus()

        return convertToBikeStations(infoList, statusList)
    }
}

@Serializable
private data class StationInformation(
    @SerialName("station_id")
    val stationId: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
)

@Serializable
private data class StationStatus(
    @SerialName("station_id")
    val stationId: String,
    @SerialName("num_bikes_available")
    val numBikesAvailable: Int,
    @SerialName("num_docks_available")
    val numDocksAvailable: Int,

)

@Serializable
private data class Response<T>(
    @SerialName("last_updated")
    val lastUpdated: Int,
    val data: ResponseData<T>,
)

@Serializable
private data class ResponseData<T> (
    val stations: List<T>,
)
