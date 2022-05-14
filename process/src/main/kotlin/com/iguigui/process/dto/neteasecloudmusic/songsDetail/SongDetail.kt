package com.iguigui.process.dto.neteasecloudmusic.songsDetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SongDetail(
    @SerialName("code")
    val code: Int,
    @SerialName("privileges")
    val privileges: List<Privilege>,
    @SerialName("songs")
    val songs: List<Song>
)