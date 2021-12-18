package com.iguigui.qqbot.bot.wechatBot.dto

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RecverImgMessageContentDTO(
    var content: String?,
    var detail: String?,
    var id1: String?,
    var id2: String?,
    var thumb: String?
)
