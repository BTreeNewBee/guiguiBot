package com.iguigui.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class HeartBeatDTO(
    var content: String?,
    var id: String?,
    var receiver: String?,
    var sender: String?,
    var srvid: Int,
    var status: String?,
    var time: String?,
    var type: Int
)