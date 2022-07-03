package com.iguigui.process.dto.tracks17.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: Data
)