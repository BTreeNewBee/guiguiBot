package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProviderX(
    @SerialName("alias")
    val alias: String,
    @SerialName("country")
    val country: String,
    @SerialName("homepage")
    val homepage: String,
    @SerialName("key")
    val key: Int,
    @SerialName("name")
    val name: String,
    @SerialName("tel")
    val tel: String?
)