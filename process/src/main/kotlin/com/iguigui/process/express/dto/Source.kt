package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    @SerialName("logo")
    val logo: String  = "",
    @SerialName("name")
    val name: String  = "",
    @SerialName("showName")
    val showName: String  = "",
    @SerialName("title")
    val title: String  = "",
    @SerialName("url")
    val url: String = ""
)