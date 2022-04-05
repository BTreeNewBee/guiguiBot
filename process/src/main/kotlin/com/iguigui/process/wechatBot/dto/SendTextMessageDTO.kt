package com.iguigui.bot.wechatBot.dto

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