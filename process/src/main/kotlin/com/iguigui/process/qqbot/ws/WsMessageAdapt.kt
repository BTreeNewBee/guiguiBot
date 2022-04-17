package com.iguigui.process.qqbot.ws

import com.iguigui.process.qqbot.MessageAdapt
import com.iguigui.process.qqbot.dto.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class WsMessageAdapt : MessageAdapt {

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
        val baseResponse = json.decodeFromString(BaseResponse::class.serializer(), message)
        println(baseResponse)
        return when (baseResponse.command) {
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.memberList -> json.decodeFromString(MemberListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            Paths.groupList -> json.decodeFromString(GroupListResponse::class.serializer(), message)
            else -> {
                BaseResponse("", "")
            }
        }
    }

}