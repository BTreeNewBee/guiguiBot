package com.iguigui.process.qqbot.ws

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.Thread.sleep
import java.net.URI

//fun main() {
//    val qqBotWsClient = QqBotWsClient()
//    qqBotWsClient.connect()
//
//    val encodeToString = MemberListRequest("984647128").toJson()
//    println(encodeToString)
//    qqBotWsClient.sendMessage(encodeToString)
//
//    val scanner = Scanner(System.`in`)
//    while (true) {
//        val baseMessage = BaseRequest(scanner.nextLine(), Content(), "", 123)
//        val encodeToString = Json.encodeToString(baseMessage)
//        println(encodeToString)
//        println()
//
//    }
//
//}

@Component
class QqBotWsClient constructor(serverURI: URI = URI("ws://192.168.50.185:8637/all?verifyKey=&qq=3633266931")) :
    WebSocketClient(serverURI) {


    val log = LogFactory.getLog(QqBotWsClient::class.java)!!

    lateinit var handler: (message: String) -> Unit

    /**
     * Json解析规则，需要注册支持的多态的类
     */
    private val json by lazy {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }


    fun sendMessage(text: String) {
        send(text)
    }


    override fun onOpen(handshakedata: ServerHandshake) {
        log.info("qq bot online ! ")
    }

    override fun onMessage(message: String) {
        handler(message)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.warn("Qq bot ws connection closed! code $code ,  reason $reason")
    }

    override fun onError(ex: Exception) {
        log.warn(ex)
    }

    fun registerHandler(handler: (message: String) -> Unit) {
        this.handler = handler
        connect()
    }


    @Scheduled(fixedDelay = 10 * 1000)
    fun checkConnection() {
        if (this.isClosed) {
            reconnect()
            log.info("try retry connect to qq bot ws server")
        }
    }

}