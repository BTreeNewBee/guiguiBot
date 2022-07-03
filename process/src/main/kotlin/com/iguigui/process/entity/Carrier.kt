package com.iguigui.process.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Carrier(
    @SerialName("country")
    val country: Int,
    @SerialName("countryIso")
    val countryIso: String,
    @SerialName("group")
    val group: String,
    @SerialName("key")
    val key: Int,
    @SerialName("name")
    val name: String,
    @SerialName("nameZhCn")
    val nameZhCn: String,
    @SerialName("nameZhHk")
    val nameZhHk: String,
    @SerialName("url")
    val url: String
)