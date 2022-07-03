package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Milestone(
    @SerialName("key_stage")
    val keyStage: String,
    @SerialName("time_iso")
    val timeIso: String?,
    @SerialName("time_utc")
    val timeUtc: String?
)