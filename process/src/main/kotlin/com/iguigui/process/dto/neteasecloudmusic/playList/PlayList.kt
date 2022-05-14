package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayList(
    @SerialName("code")
    val code: Int,
    @SerialName("playlist")
    val playlist: PlaylistX,
    @SerialName("privileges")
    val privileges: List<Privilege>
)