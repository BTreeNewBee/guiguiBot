package com.iguigui.process.dto.neteasecloudmusic.search


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Song(
    @SerialName("album")
    val album: Album,
    @SerialName("alias")
    val alias: List<String>,
    @SerialName("artists")
    val artists: List<ArtistX>,
    @SerialName("copyrightId")
    val copyrightId: Int,
    @SerialName("duration")
    val duration: Int,
    @SerialName("fee")
    val fee: Int,
    @SerialName("ftype")
    val ftype: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("mark")
    val mark: Long,
    @SerialName("mvid")
    val mvid: Int,
    @SerialName("name")
    val name: String,
    @SerialName("rtype")
    val rtype: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("transNames")
    val transNames: List<String> = Collections.emptyList(),
)