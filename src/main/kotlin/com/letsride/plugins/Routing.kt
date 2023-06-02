package com.letsride.plugins

import com.letsride.models.BikeStation
import com.letsride.services.CityBikeService
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
enum class Sorting {
    @SerialName("name")
    Name,

    @SerialName("bikes")
    Bikes,

    @SerialName("vacancies")
    Vacancies,
}

@Serializable
@Resource("/list")
class StationListRoute(val sort: Sorting = Sorting.Name)

fun Application.configureRouting() {
    val cityBikeService by inject<CityBikeService>()

    routing {
        get("/") {
            call.respondText("Oslo City Bike API")
        }
        get<StationListRoute> { query ->
            val unsortedStations = cityBikeService.getStationList()
            val stations = when (query.sort) {
                Sorting.Name -> unsortedStations.sortedBy(BikeStation::name)
                Sorting.Bikes -> unsortedStations.sortedBy(BikeStation::availableBikes).reversed()
                Sorting.Vacancies -> unsortedStations.sortedBy(BikeStation::vacantSlots).reversed()
            }

            call.respond(stations)
        }
    }
}
