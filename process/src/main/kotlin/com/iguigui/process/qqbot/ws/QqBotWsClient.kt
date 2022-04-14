package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.dto.request.memberList.MemberListRequest
import com.iguigui.process.qqbot.dto.request.toJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.springframework.stereotype.Component
import java.lang.Exception
import java.net.URI
import java.util.Scanner
import javax.annotation.PostConstruct

fun main() {
    val qqBotWsClient = QqBotWsClient()
    qqBotWsClient.connect()
    Thread.sleep(1000)

    val encodeToString = MemberListRequest("984647128").toJson()
    println(encodeToString)
    qqBotWsClient.sendMessage(encodeToString)

    val scanner = Scanner(System.`in`)
    while (true) {
//        val baseMessage = BaseCommandMessage(scanner.nextLine(), Content(), "", 123)
//        val encodeToString = Json.encodeToString(baseMessage)
        println(encodeToString)
        println()
        qqBotWsClient.sendMessage(encodeToString)
    }

}

@Component
class QqBotWsClient constructor(serverURI: URI = URI("ws://192.168.50.185:8637/all?verifyKey=&qq=3633266931")) : WebSocketClient(serverURI) {


    val log = LogFactory.getLog(QqBotWsClient::class.java)!!

    @PostConstruct
    fun start() {
        send("")
    }

    fun sendMessage(text : String) {
        send(text)
    }


    override fun onOpen(handshakedata: ServerHandshake) {
        log.info("qq bot online ! ")
    }

    override fun onMessage(message: String) {
        println(message)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.warn("Qq bot ws connection closed! code $code ,  reason $reason")
    }

    override fun onError(ex: Exception) {
        log.warn(ex)
    }


}