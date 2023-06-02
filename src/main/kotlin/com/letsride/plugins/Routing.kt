package com.letsride.plugins

import com.letsride.models.BikeStation
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import kotlinx.serialization.SerialName

@Serializable
enum class Sorting {
    @SerialName("name")
    Name,
    @SerialName("bikes")
    Bikes,
    @SerialName("vacancies")
    Vacancies
}

@Serializable
@Resource("/list")
class StationListRoute(val sort: Sorting = Sorting.Name)


fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/") {
            call.respondText("Oslo City Bike API")
        }
        get<StationListRoute> { query ->
            val unsortedStations = listOf(
                BikeStation("123", "acme station", "address", 23.4, 23.1, 5, 3, 2),
                BikeStation("123", "nameless", "address", 23.4, 23.1, 5, 0, 5),
                BikeStation("123", "foobar", "address", 23.4, 23.1, 5, 4, 1),
            )
            val stations = when (query.sort) {
                Sorting.Name -> unsortedStations.sortedBy(BikeStation::name)
                Sorting.Bikes -> unsortedStations.sortedBy(BikeStation::availableBikes).reversed()
                Sorting.Vacancies -> unsortedStations.sortedBy(BikeStation::vacantSlots).reversed()
            }

            call.respond(stations)
        }
    }
}

