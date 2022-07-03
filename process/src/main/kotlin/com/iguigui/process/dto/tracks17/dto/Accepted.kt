package com.iguigui.process.dto.tracks17.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Accepted(
    @SerialName("carrier")
    val carrier: Int,
    @SerialName("number")
    val number: String,
    @SerialName("origin")
    val origin: Int,
    @SerialName("tag")
    val tag: String? = null
)