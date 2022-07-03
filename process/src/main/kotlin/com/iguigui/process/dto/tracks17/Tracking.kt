package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tracking(
    @SerialName("providers")
    val providers: List<Provider>,
    @SerialName("providers_hash")
    val providersHash: Int
)