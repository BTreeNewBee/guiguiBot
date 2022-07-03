package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebHookTracks17(
    @SerialName("data")
    val `data`: TrackData,
    @SerialName("event")
    val event: String
)