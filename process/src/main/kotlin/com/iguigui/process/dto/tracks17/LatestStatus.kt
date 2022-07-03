package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestStatus(
    @SerialName("status")
    val status: String,
    @SerialName("sub_status")
    val subStatus: String,
    @SerialName("sub_status_descr")
    val subStatusDescr: String?
)