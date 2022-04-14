package com.iguigui.process.qqbot.dto.response.memberList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: List<MemberListInfo>,
    @SerialName("msg")
    val msg: String
)