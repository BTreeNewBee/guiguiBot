package com.iguigui.process.service

import cn.hutool.http.HttpUtil
import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class BootMyPC {

    @Value("\${sgdz_account}")
    lateinit var account: String

    @Value("\${sgdz_password}")
    lateinit var password: String


    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @SubscribeBotMessage(functionName = "开机小助手")
    fun boot(dto: GroupMessagePacketDTO) {
        if (dto.sender.group.id != 727948097L) {
            return
        }
        val contentToString = dto.contentToString()
        val url = "https://songguoyun.topwd.top/Esp_Api_new.php"
        if (contentToString == "开机") {
            val body = Body(account, password, "9527", "1")
            val post = HttpUtil.post(url, Json.encodeToString(Body.serializer(), body))
            Json.parseToJsonElement(post).jsonObject["status"]?.jsonPrimitive?.content?.let {
                messageAdapter.sendGroupMessage(dto.sender.group.id, if(it == "0") "发送成功" else "发送失败")
            }
        }
        if (contentToString == "关机") {
            val body = Body(account, password, "9527", "0")
            val post = HttpUtil.post(url, Json.encodeToString(Body.serializer(), body))
            Json.parseToJsonElement(post).jsonObject["status"]?.jsonPrimitive?.content?.let {
                messageAdapter.sendGroupMessage(dto.sender.group.id, if(it == "0") "发送成功" else "发送失败")
            }
        }
        if (contentToString == "状态") {
            val body = Body(account, password, "9527", "11")
            val post = HttpUtil.post(url, Json.encodeToString(Body.serializer(), body))
            Json.parseToJsonElement(post).jsonObject["status"]?.jsonPrimitive?.content?.let {
                messageAdapter.sendGroupMessage(dto.sender.group.id, if(it == "1") "已开机" else "已关机")
            }
        }

    }

    @Serializable
    data class Body(
        val sgdz_account: String,
        val sgdz_password: String,
        val device_name: String,
        val value: String
    )

}