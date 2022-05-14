package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackId(
    @SerialName("at")
    val at: Long,
    @SerialName("id")
    val id: Int,
    @SerialName("rcmdReason")
    val rcmdReason: String,
    @SerialName("t")
    val t: Int,
    @SerialName("uid")
    val uid: Int,
    @SerialName("v")
    val v: Int
)