package com.iguigui.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomMemberNickDTO(
    val wxid: String,
    val nick: String,
    val roomid: String
)