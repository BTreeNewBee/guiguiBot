package com.iguigui.process.qqbot.dto.request.sendFriendMessage


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageChain(
    @SerialName("text")
    val text: String,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)