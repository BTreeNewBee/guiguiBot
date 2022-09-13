package com.iguigui.plugins

import com.iguigui.dto.HeartBeat
import com.iguigui.dto.StartResponse
import com.iguigui.dto.wapperToHttpResponse
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/submitBandInfo") {
            with(call.receive<HeartBeat>()) {
                call.respond(StartResponse("OK",true).wapperToHttpResponse())
            }
        }
    }
}
