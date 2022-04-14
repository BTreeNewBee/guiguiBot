package com.iguigui.process.qqbot.dto.response.groupList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupListInfo(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("permission")
    val permission: String
)