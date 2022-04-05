package com.iguigui.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecverImgMessageContentDTO(
    var content: String?,
    var detail: String?,
    var id1: String?,
    var id2: String?,
    var thumb: String?
)
