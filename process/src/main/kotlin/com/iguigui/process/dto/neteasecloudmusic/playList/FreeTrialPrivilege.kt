package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeTrialPrivilege(
    @SerialName("resConsumable")
    val resConsumable: Boolean,
    @SerialName("userConsumable")
    val userConsumable: Boolean
)