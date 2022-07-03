package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordinatesXX(
    @SerialName("latitude")
    val latitude: String?,
    @SerialName("longitude")
    val longitude: String?
)