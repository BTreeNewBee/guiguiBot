package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Company(
    @SerialName("auxiliary")
    val auxiliary: List<Auxiliary>,
    @SerialName("fullname")
    val fullname: String = "",
    @SerialName("icon")
    val icon: Icon?,
    @SerialName("icon249")
    val icon249: String = "",
    @SerialName("logo_color")
    val logoColor: String = "",
    @SerialName("shortname")
    val shortname: String = "",
    @SerialName("tel")
    val tel: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("website")
    val website: Website?
)