package com.iguigui.process.qqbot.dto.response


import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
open class BaseResponse(
    val syncId: String?,
    val command: String,
)

fun BaseResponse.toJson(): String = Json.encodeToString(this)
