package com.iguigui.process.qqbot.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
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


@Serializable
data class MemberListRequest (val target: String) : BaseRequest(
    command = "memberList",
    content = MemberListRequestContent(target),
    subCommand = "",
    syncId = 123
)

@Serializable
data class MemberListRequestContent (val target:String) : Content()


