package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Icon(
    @SerialName("id")
    val id: String  = "",
    @SerialName("middlepos")
    val middlepos: String  = "",
    @SerialName("middleurl")
    val middleurl: String  = "",
    @SerialName("normal")
    val normal: String  = "",
    @SerialName("smallpos")
    val smallpos: String  = "",
    @SerialName("smallurl")
    val smallurl: String = ""
)