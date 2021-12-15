package com.iguigui.qqbot.bot.wechatBot.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserListDTO(
    var content: List<Content>?,
    var id: String?,
    var type: Int
)