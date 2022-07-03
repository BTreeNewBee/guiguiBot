package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackData(
    @SerialName("carrier")
    val carrier: Int,
    @SerialName("number")
    val number: String,
    @SerialName("param")
    val `param`: String?,
    @SerialName("tag")
    val tag: String?,
    @SerialName("track_info")
    val trackInfo: TrackInfo
)