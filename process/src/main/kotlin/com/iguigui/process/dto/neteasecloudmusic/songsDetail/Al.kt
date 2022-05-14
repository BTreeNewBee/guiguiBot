package com.iguigui.process.dto.neteasecloudmusic.songsDetail


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Al(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("pic")
    val pic: Long,
    @SerialName("pic_str")
    val picStr: String = "",
    @SerialName("picUrl")
    val picUrl: String,
    @SerialName("tns")
    val tns: List<String>
)