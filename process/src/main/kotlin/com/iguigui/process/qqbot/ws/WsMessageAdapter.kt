package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


fun main() {
    val classLoader = String.javaClass.classLoader
    val packageName = "com.iguigui.process.qqbot.dto";
    val packageResource: String = packageName.replace(".", "/")
    val url: URL = classLoader.getResource(packageResource)
    val root = File(url.toURI())
    val classList: MutableList<String> = ArrayList()
    scanClassesInner(root, packageName, classList)

    println(classList)

}

private fun scanClassesInner(root: File, packageName: String, result: MutableList<String>) {
    for (child in Objects.requireNonNull(root.listFiles())) {
        val name = child.name
        if (child.isDirectory) {
            scanClassesInner(child, "$packageName.$name", result)
        } else if (name.endsWith(".class")) {
            val className = packageName + "." + name.replace(".class", "")
            result.add(className)
        }
    }
}

@Component
class WsMessageAdapter : MessageAdapter {

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

    @Autowired
    lateinit var qqBotWsClient: QqBotWsClient

    lateinit var handler: (message: BaseResponse) -> Unit

    override fun registerHandler(handler: (message: BaseResponse) -> Unit) {
        this.handler = handler
        qqBotWsClient.registerHandler(this::handlerMessage)
    }

    override fun sendMessage(message: BaseRequest) {
        qqBotWsClient.sendMessage(message.toJson())
    }

    private fun handlerMessage(message: String) {
        this.handler(messageConverter(message))
    }

    @OptIn(InternalSerializationApi::class)
    private fun messageConverter(message: String): BaseResponse {
        val parseToJsonElement = json.parseToJsonElement(message)
        val command = parseToJsonElement.jsonObject["command"].toString()
        val baseResponse = json.decodeFromString(BaseResponse::class.serializer(), message)
        println("baseResponse ${baseResponse.toJson()}")
        return when (command) {
            Paths.reservedMessage -> reservedMessageConverter(message)
            else -> {
                commandMessageConverter(message)
            }
        }
    }

    private fun reservedMessageConverter(message: String) : BaseResponse {

        return BaseResponse("", "",null)
    }

    private fun commandMessageConverter(message: String) : BaseResponse {

        return BaseResponse("", "",null)
    }

}