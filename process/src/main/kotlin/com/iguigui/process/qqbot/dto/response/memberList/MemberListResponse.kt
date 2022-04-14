package com.iguigui.process.qqbot.dto.response.memberList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberListResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("syncId")
    val syncId: String
)