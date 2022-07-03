package com.iguigui.process.dto.tracks17.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("accepted")
    val accepted: List<Accepted>,
    @SerialName("rejected")
    val rejected: List<Rejected>
)