package com.iguigui.bot.wechatBot.dto

import kotlinx.serialization.Serializable

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
