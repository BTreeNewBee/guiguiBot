package com.iguigui.process.dto.tracks17.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rejected(
    @SerialName("error")
    val error: Error,
    @SerialName("number")
    val number: String,
    @SerialName("tag")
    val tag: String? = null
)