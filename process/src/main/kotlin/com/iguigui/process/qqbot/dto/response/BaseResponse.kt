package com.iguigui.process.qqbot.dto.response


import com.iguigui.process.qqbot.dto.request.BaseRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
open class BaseResponse(
    val syncId: String?,
    val command: String,
)

fun BaseRequest.toJson(): String = Json.encodeToString(this)
