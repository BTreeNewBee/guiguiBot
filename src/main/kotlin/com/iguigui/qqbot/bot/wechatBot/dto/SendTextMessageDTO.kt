package com.iguigui.qqbot.bot.wechatBot.dto

import com.iguigui.qqbot.bot.wechatBot.Constant
import com.iguigui.qqbot.bot.wechatBot.WechatBot
import kotlinx.serialization.Serializable

@Serializable
data class SendTextMessageDTO (
    val wxid: String,
    val content: String,
    val roomid: String,
    val nickname: String,
    val ext: String,
    val type: Int ,
    val id: String
        )