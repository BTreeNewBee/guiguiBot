package com.iguigui.process.qqbot.dto.response.memberList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberListInfo(
    @SerialName("group")
    val group: Group,
    @SerialName("id")
    val id: Int,
    @SerialName("joinTimestamp")
    val joinTimestamp: Int,
    @SerialName("lastSpeakTimestamp")
    val lastSpeakTimestamp: Int,
    @SerialName("memberName")
    val memberName: String,
    @SerialName("muteTimeRemaining")
    val muteTimeRemaining: Int,
    @SerialName("permission")
    val permission: String,
    @SerialName("specialTitle")
    val specialTitle: String
)