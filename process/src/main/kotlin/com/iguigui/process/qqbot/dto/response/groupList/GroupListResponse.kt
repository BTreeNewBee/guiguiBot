package com.iguigui.process.qqbot.dto.response.groupList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupListResponse(
    @SerialName("data")
    val `data`: Data,
    @SerialName("syncId")
    val syncId: String
)