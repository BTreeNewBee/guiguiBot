package com.iguigui.plugins

import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.slf4j.event.Level

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        })
    }

    //Provides the ability to receive a request body several times.
    install(DoubleReceive) {
    }
    //Logging request body.
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod
            val queryParameters = call.request.rawQueryParameters
            when (httpMethod) {
                HttpMethod.Get -> "Status: $status, HTTP method: $httpMethod, Uri: ${call.request.uri}, QueryParameters: $queryParameters "
                HttpMethod.Post -> runBlocking {
                    "Status: $status, HTTP method: $httpMethod, Uri: ${call.request.uri} ,Body: ${
                        call.receive<JsonElement>().toString()
                    } "
                }
                else -> {
                    "Status: $status, HTTP method: $httpMethod, Uri: ${call.request.uri}, QueryParameters: $queryParameters "
                }
            }
        }
    }
}
