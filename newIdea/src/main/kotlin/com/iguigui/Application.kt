package com.iguigui

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.iguigui.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSockets()
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
