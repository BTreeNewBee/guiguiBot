package com.iguigui.process.qqbot.dto.request

import com.iguigui.process.qqbot.dto.request.memberList.MemberListRequestContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
open class BaseRequest(
    val command: String,
    val content: MemberListRequestContent,
    val subCommand: String,
    val syncId: Int
)

fun BaseRequest.toJson(): String = Json.encodeToString(this)