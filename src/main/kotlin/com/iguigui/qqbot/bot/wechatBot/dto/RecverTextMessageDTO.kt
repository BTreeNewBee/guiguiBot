package com.iguigui.qqbot.bot.wechatBot.dto

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RecverTextMessageDTO(
    var content: String?,
    var id: String?,
    var id1: String?,
    var id2: String?,
    var id3: String?,
    var srvid: Int,
    var time: String?,
    var type: Int,
    var wxid: String?
)
