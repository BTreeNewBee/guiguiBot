package com.iguigui.process.dto.tracks17.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("number")
    val number: String,
    @SerialName("carrier")
    val carrier: Int? = null,
    @SerialName("param")
    val `param`: String? = null
)