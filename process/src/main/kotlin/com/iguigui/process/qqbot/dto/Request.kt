package com.iguigui.process.qqbot.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.mamoe.mirai.message.data.MessageChain
import kotlin.reflect.KClass


@Serializable
sealed class Content

@Serializable
sealed class BaseRequest(
    open val command: String,
    open val content: Content,
    open val subCommand: String,
    open val syncId: Int
)

fun BaseRequest.toJson(): String = Json.encodeToString(this)

//获取群成员列表
@Serializable
data class MemberListRequest(val target: Long) : BaseRequest(
    command = "memberList",
    content = MemberListRequestContent(target),
    subCommand = "",
    syncId = 123
)

@Serializable
data class MemberListRequestContent(val target: Long) : Content()

//群消息请求
@Serializable
data class GroupMessageRequest(val id: Long, val messageChain: List<MessageDTO>) : BaseRequest(
    command = "sendGroupMessage",
    content = GroupMessageRequestContent(id, messageChain),
    subCommand = "",
    syncId = 1
)


//发送群消息
@Serializable
data class GroupMessageRequestContent(
    val target: Long,
    val messageChain: List<MessageDTO>
) : Content()


