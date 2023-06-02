package com.letsride

import com.letsride.plugins.*
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.resources.*
import org.koin.ktor.plugin.Koin

// fun main(args: Array<String>): Unit = EngineMain.main(args)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    /**
     *  This will throw an exception if the configuration value is not found,
     *  which is okay since the app cannot function properly without it
     */
    val clientIdentifier = ConfigFactory.load().getString("letsride.clientIdentifier")
    install(Koin) {
        modules(getInjectionModules(clientIdentifier))
    }

    install(Resources)
    configureRouting()
    configureSerialization()
}
