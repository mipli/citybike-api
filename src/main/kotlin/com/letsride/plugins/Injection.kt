package com.letsride.plugins

import com.letsride.services.CityBikeService
import com.letsride.services.CityBikeServiceImpl
import io.ktor.client.engine.cio.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun getInjectionModules(clientIdentifier: String): Module {
    return module {
        single<CityBikeService> { CityBikeServiceImpl(clientIdentifier, CIO.create()) }
    }
}
