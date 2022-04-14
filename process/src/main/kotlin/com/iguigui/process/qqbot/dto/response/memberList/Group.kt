package com.iguigui.process.qqbot.dto.response.memberList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("permission")
    val permission: String
)