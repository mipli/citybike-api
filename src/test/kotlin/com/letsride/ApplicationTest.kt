package com.letsride

import com.letsride.models.BikeStation
import com.letsride.plugins.*
import com.letsride.services.CityBikeService
import com.letsride.services.CityBikeServiceImpl
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.*

private fun createMockEngine(): MockEngine {
    return MockEngine { request ->
        if (request.url.toString().contains("station_information")) {
            respond(
                content = """{
                      "last_updated": 1553592653,
                      "data": {
                        "stations": [
                          {  
                            "station_id":"175",
                            "name":"Skøyen Stasjon",
                            "address":"Skøyen Stasjon",
                            "lat":59.9226729,
                            "lon":10.6788129,
                            "capacity":20
                          },
                          {  
                            "station_id":"47",
                            "name":"7 Juni Plassen",
                            "address":"7 Juni Plassen",
                            "lat":59.9150596,
                            "lon":10.7312715,
                            "capacity":15
                          },
                          {  
                            "station_id":"10",
                            "name":"Sotahjørnet",
                            "address":"Sotahjørnet",
                            "lat":59.9099822,
                            "lon":10.7914482,
                            "capacity":20
                          }
                        ]
                      }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        } else if (request.url.toString().contains("station_status")) {
            respond(
                content = """{
                      "last_updated": 1540219230,
                      "data": {
                        "stations": [
                          {
                            "is_installed": 1,
                            "is_renting": 1,
                            "num_bikes_available": 5,
                            "num_docks_available": 0,
                            "last_reported": 1540219230,
                            "is_returning": 1,
                            "station_id": "175"
                          },
                          {
                            "is_installed": 1,
                            "is_renting": 1,
                            "num_bikes_available": 2,
                            "num_docks_available": 3,
                            "last_reported": 1540219230,
                            "is_returning": 1,
                            "station_id": "47"
                          },
                          {
                            "is_installed": 1,
                            "is_renting": 1,
                            "num_bikes_available": 0,
                            "num_docks_available": 5,
                            "last_reported": 1540219230,
                            "is_returning": 1,
                            "station_id": "10"
                          }
                        ]
                      }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        } else {
            respond(
                content = "",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
    }
}

class ApplicationTest : KoinTest {
    @Before
    fun setUp() {
        startKoin {
            modules(
                module {
                    single<CityBikeService> { CityBikeServiceImpl("test-client", createMockEngine()) }
                },
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testRoot() = testApplication {
        application {
            install(Resources)
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Oslo City Bike API", bodyAsText())
        }
    }

    @Test
    fun testFetchStationList() = testApplication {
        application {
            install(Resources)
            configureRouting()
            configureSerialization()
        }

        client.get("/list").apply {
            assertEquals(HttpStatusCode.OK, status)
            val stations: List<BikeStation> = Json.decodeFromString(bodyAsText())
            assertEquals(3, stations.size)
            assertEquals("7 Juni Plassen", stations[0].name)
        }
    }

    @Test
    fun testFetchStationListSortedVacancies() = testApplication {
        application {
            install(Resources)
            configureRouting()
            configureSerialization()
        }

        client.get("/list?sort=vacancies").apply {
            assertEquals(HttpStatusCode.OK, status)
            val stations: List<BikeStation> = Json.decodeFromString(bodyAsText())
            assertEquals(3, stations.size)
            assertEquals("Sotahjørnet", stations[0].name)
        }
    }

    @Test
    fun testFetchStationListSortedBikes() = testApplication {
        application {
            install(Resources)
            configureRouting()
            configureSerialization()
        }

        client.get("/list?sort=bikes").apply {
            assertEquals(HttpStatusCode.OK, status)
            val stations: List<BikeStation> = Json.decodeFromString(bodyAsText())
            assertEquals(3, stations.size)
            assertEquals("Skøyen Stasjon", stations[0].name)
        }
    }

    @Test
    fun testFetchStationListSortedPosition() = testApplication {
        application {
            install(Resources)
            configureRouting()
            configureSerialization()
        }

        client.get("/list?sort=pos&lat=59.9150596&lon=10.7312716").apply {
            assertEquals(HttpStatusCode.OK, status)
            val stations: List<BikeStation> = Json.decodeFromString(bodyAsText())
            assertEquals(3, stations.size)
            assertEquals("7 Juni Plassen", stations[0].name)
        }
    }
}
