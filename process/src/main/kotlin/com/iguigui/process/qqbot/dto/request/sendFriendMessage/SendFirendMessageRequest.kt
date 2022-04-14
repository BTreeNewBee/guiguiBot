package com.iguigui.process.qqbot.dto.request.sendFriendMessage


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendFirendMessageRequest(
    @SerialName("messageChain")
    val messageChain: List<MessageChain>,
    @SerialName("sessionKey")
    val sessionKey: String,
    @SerialName("target")
    val target: Int
)