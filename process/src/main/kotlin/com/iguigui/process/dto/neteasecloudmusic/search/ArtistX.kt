package com.iguigui.process.dto.neteasecloudmusic.search


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtistX(
    @SerialName("albumSize")
    val albumSize: Int,
    @SerialName("alias")
    val alias: List<String>,
    @SerialName("id")
    val id: Int,
    @SerialName("img1v1")
    val img1v1: Int,
    @SerialName("img1v1Url")
    val img1v1Url: String,
    @SerialName("name")
    val name: String,
    @SerialName("picId")
    val picId: Int
)