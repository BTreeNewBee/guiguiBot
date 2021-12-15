package com.iguigui.qqbot.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    var headimg: String?,
    var name: String?,
    var node: Long,
    var remarks: String?,
    var wxcode: String?,
    var wxid: String?
)