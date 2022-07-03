package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EstimatedDeliveryDate(
    @SerialName("from")
    val from: String?,
    @SerialName("source")
    val source: String?,
    @SerialName("to")
    val to: String?
)