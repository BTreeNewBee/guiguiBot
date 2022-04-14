package com.iguigui.process.qqbot.dto.response.friendList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirendListInfo(
    @SerialName("id")
    val id: Int,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("remark")
    val remark: String
)