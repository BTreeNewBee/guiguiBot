package com.iguigui.process.dto.neteasecloudmusic.search


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Collections

@Serializable
data class Album(
    @SerialName("alia")
    val alia: List<String> = Collections.emptyList(),
    @SerialName("artist")
    val artist: Artist,
    @SerialName("copyrightId")
    val copyrightId: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("mark")
    val mark: Int,
    @SerialName("name")
    val name: String,
    @SerialName("picId")
    val picId: Long,
    @SerialName("publishTime")
    val publishTime: Long,
    @SerialName("size")
    val size: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("transNames")
    val transNames: List<String> = Collections.emptyList(),
)