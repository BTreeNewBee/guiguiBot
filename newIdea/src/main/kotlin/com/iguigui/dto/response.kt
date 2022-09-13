package com.iguigui.dto

import com.iguigui.util.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


@Serializable
data class HttpResponse(
    @SerialName("data")
    val data: JsonElement = EmptyBaseData.toJsonElement(),
    @SerialName("code")
    val code: Int = 0,
    @SerialName("message")
    val message: String = "success"
)

@Serializable
data class WsResponse(
    @SerialName("data")
    val data: JsonElement,
    @SerialName("command")
    val command: String,
    @SerialName("msgId")
    val msgId: Int = 0,
    @SerialName("code")
    val code: Int = 0,
    @SerialName("message")
    val message: String = "success"
)

@Serializable
sealed class BaseData

@Serializable
data class StartResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()


@Serializable
data class StopResponse(
    @SerialName("id")
    val id: String,
    @SerialName("result")
    val result: Boolean
) : BaseData()



@Serializable
object EmptyBaseData : BaseData()

private fun BaseData.toJsonElement() =
    Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" })


fun BaseData.wapperToHttpResponse() =
    HttpResponse(Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" }))

fun BaseData.wapperToWsResponse(command: String, msgId: Int) =
    WsResponse(
        Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" }),
        command,
        msgId
    )

fun WsResponse.toJsonString() = Json.encodeToJsonElement(Json.encodeToJsonElement(this).jsonObject.filterNot { it.key == "type" }).toString()

