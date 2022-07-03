package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordinatesX(
    @SerialName("latitude")
    val latitude: String?,
    @SerialName("longitude")
    val longitude: String?
)