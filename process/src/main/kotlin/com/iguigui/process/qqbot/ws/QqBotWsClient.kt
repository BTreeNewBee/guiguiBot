package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.dto.request.BaseRequest
import com.iguigui.process.qqbot.dto.request.Content
import com.iguigui.process.qqbot.dto.request.memberList.MemberListRequest
import com.iguigui.process.qqbot.dto.request.toJson
import com.iguigui.process.qqbot.dto.response.BaseResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.apache.commons.logging.LogFactory
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception
import java.lang.Thread.sleep
import java.net.URI
import java.util.Scanner
import javax.annotation.PostConstruct

fun main() {
    val qqBotWsClient = QqBotWsClient()
    qqBotWsClient.connect()

    val encodeToString = MemberListRequest("984647128").toJson()
    println(encodeToString)
    qqBotWsClient.sendMessage(encodeToString)

    val scanner = Scanner(System.`in`)
    while (true) {
        val baseMessage = BaseRequest(scanner.nextLine(), Content(), "", 123)
        val encodeToString = Json.encodeToString(baseMessage)
        println(encodeToString)
        println()

    }

}

@Component
class QqBotWsClient constructor(serverURI: URI = URI("ws://192.168.50.185:8637/all?verifyKey=&qq=3633266931")) : WebSocketClient(serverURI) {


    val log = LogFactory.getLog(QqBotWsClient::class.java)!!

    val retryConnectionMaxTimes = 5

    var retryConnectionTimes = 0

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



    fun sendMessage(text : String) {
        send(text)
    }


    override fun onOpen(handshakedata: ServerHandshake) {
        log.info("qq bot online ! ")
        retryConnectionTimes = 0
    }

    @OptIn(InternalSerializationApi::class)
    override fun onMessage(message: String) {
        println(message)
        handler(message)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log.warn("Qq bot ws connection closed! code $code ,  reason $reason")
        retryConnection()
    }

    override fun onError(ex: Exception) {
        log.warn(ex)
        retryConnection()
    }

    fun registerHandler(handler: (message: String) -> Unit) {
        this.handler = handler
        connect()
    }


    private fun retryConnection() {
        if (retryConnectionTimes < retryConnectionMaxTimes) {
            runBlocking {
                sleep(1000 * 10)
                connect()
            }
        }
    }

}