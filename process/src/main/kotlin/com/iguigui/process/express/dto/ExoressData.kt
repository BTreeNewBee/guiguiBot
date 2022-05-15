package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExoressData(
    @SerialName("com")
    val com: String  = "",
    @SerialName("company")
    val company: Company,
    @SerialName("info")
    val info: Info,
    @SerialName("kuaidiSource")
    val kuaidiSource: KuaidiSource,
    @SerialName("notice")
    val notice: String  = "",
    @SerialName("source")
    val source: Source
)