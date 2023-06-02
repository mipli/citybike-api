package com.letsride.plugins

import com.letsride.models.BikeStation
import com.letsride.models.Position
import com.letsride.services.CityBikeService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import kotlin.math.*

@Serializable
enum class Sorting {
    @SerialName("name")
    Name,

    @SerialName("bikes")
    Bikes,

    @SerialName("vacancies")
    Vacancies,

    @SerialName("pos")
    Position,
}

@Serializable
@Resource("/list")
class StationListRoute(val sort: Sorting = Sorting.Name, val lon: Double? = null, val lat: Double? = null)

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
                Sorting.Position -> {
                    val longitude = query?.lon ?: return@get call.respond(HttpStatusCode.BadRequest, "Longitude required when sorting by position")
                    val latitude = query?.lat ?: return@get call.respond(HttpStatusCode.BadRequest, "Latitude required when sorting by position")
                    val pos = Position(longitude, latitude)
                    unsortedStations.sortedWith { a, b ->
                        val aDelta = pos.distanceTo(a.position)
                        val bDelta = pos.distanceTo(b.position)
                        aDelta.compareTo(bDelta)
                    }
                }
            }

            call.respond(stations)
        }
    }
}
