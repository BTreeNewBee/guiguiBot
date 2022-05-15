package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Context(
    @SerialName("desc")
    val desc: String = "",
    @SerialName("time")
    val time: String = ""
)