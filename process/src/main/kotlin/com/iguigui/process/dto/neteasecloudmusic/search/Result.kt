package com.iguigui.process.dto.neteasecloudmusic.search


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("hasMore")
    val hasMore: Boolean,
    @SerialName("songCount")
    val songCount: Int,
    @SerialName("songs")
    val songs: List<Song>
)