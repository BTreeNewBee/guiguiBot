package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class M(
    @SerialName("br")
    val br: Int,
    @SerialName("fid")
    val fid: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("sr")
    val sr: Int,
    @SerialName("vd")
    val vd: Int
)