package com.iguigui.process.qqbot.dto.response.appDTO


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("ctime")
    val ctime: Int,
    @SerialName("forward")
    val forward: Boolean,
    @SerialName("token")
    val token: String,
    @SerialName("type")
    val type: String
)