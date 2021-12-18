package com.iguigui.qqbot.bot.wechatBot.dto

import com.iguigui.qqbot.bot.wechatBot.Constant
import com.iguigui.qqbot.bot.wechatBot.WechatBot
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomMemberNickDTO(
    val wxid: String,
    val nick: String,
    val roomid: String
)