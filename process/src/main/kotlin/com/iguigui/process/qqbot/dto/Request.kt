package com.iguigui.process.qqbot.dto

import com.iguigui.process.qqbot.dto.request.Content
import com.iguigui.process.qqbot.dto.request.memberList.MemberListRequestContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
open class BaseRequest(
    val command: String,
    val content: Content,
    val subCommand: String,
    val syncId: Int
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


