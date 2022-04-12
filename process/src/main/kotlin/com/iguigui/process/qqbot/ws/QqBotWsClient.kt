package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.dto.BaseCommandMessage
import com.iguigui.process.qqbot.dto.Content
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.springframework.stereotype.Component
import java.lang.Exception
import java.net.URI
import javax.annotation.PostConstruct

fun main() {
    val qqBotWsClient = QqBotWsClient()
    qqBotWsClient.connect()
    runBlocking {
        delay(5*1000)
        val baseMessage = BaseCommandMessage("memberProfile", Content(), "", 123)
        val encodeToString = Json.encodeToString(baseMessage)
        println(encodeToString)
        qqBotWsClient.sendMessage(encodeToString)
    }
    System.`in`.read()
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
        log.info("qq bot receviver message $message")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.warn("Qq bot ws connection closed! code $code ,  reason $reason")
    }

    override fun onError(ex: Exception) {
        log.warn(ex)
    }


}