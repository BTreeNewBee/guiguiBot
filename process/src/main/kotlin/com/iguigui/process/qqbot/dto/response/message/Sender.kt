package com.iguigui.process.qqbot.dto.response.message

data class Sender(
    val group: Group,
    val id: Int,
    val joinTimestamp: Int,
    val lastSpeakTimestamp: Int,
    val memberName: String,
    val muteTimeRemaining: Int,
    val permission: String,
    val specialTitle: String
)