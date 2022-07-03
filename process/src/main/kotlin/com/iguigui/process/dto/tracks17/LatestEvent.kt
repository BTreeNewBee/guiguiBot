package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestEvent(
    @SerialName("address")
    val address: Address,
    @SerialName("description")
    val description: String,
    @SerialName("location")
    val location: String,
    @SerialName("stage")
    val stage: String?,
    @SerialName("time_iso")
    val timeIso: String,
    @SerialName("time_utc")
    val timeUtc: String
)