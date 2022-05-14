package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargeInfo(
    @SerialName("chargeType")
    val chargeType: Int,
    @SerialName("rate")
    val rate: Int
)