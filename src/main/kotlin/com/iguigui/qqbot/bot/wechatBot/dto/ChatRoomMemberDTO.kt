package com.iguigui.qqbot.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomMemberDTO(
    var address: Long,
    var member: List<String>,
    var room_id: String
)