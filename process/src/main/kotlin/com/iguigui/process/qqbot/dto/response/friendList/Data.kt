package com.iguigui.process.qqbot.dto.response.friendList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: List<FirendListInfo>,
    @SerialName("msg")
    val msg: String
)