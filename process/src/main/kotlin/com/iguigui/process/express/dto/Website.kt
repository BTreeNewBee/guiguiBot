package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Website(
    @SerialName("title")
    val title: String  = "",
    @SerialName("url")
    val url: String = ""
)