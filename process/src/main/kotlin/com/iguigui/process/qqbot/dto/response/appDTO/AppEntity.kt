package com.iguigui.process.qqbot.dto.response.appDTO


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppEntity(
    @SerialName("app")
    val app: String,
    @SerialName("config")
    val config: Config,
    @SerialName("desc")
    val desc: String,
    @SerialName("extra")
    val extra: Extra,
    @SerialName("meta")
    val meta: Meta,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("ver")
    val ver: String,
    @SerialName("view")
    val view: String
)