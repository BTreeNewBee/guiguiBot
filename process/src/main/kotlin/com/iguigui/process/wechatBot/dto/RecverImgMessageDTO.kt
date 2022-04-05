package com.iguigui.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecverImgMessageDTO(
    var content: RecverImgMessageContentDTO?,
    var id: String?,
    var receiver: String?,
    var sender: String?,
    var srvid: Int,
    var status: String?,
    var time: String?,
    var type: Int,
)
