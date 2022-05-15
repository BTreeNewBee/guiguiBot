package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpressResult(
    @SerialName("data")
    val `data`: ExoressData,
    @SerialName("error_code")
    val errorCode: String  = "",
    @SerialName("msg")
    val msg: String  = "",
    @SerialName("status")
    val status: String = ""
)