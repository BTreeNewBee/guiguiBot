package com.iguigui.process.qqbot.dto.response.friendList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendListResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("syncId")
    val syncId: String
)