package com.iguigui.qqbot.bot.wechatBot

import kotlinx.serialization.Serializable

@Serializable
data class WeChatMessageDTO(val type: Int,
                            val wxid: String,
                            val content: String,
                            val id: String = System.currentTimeMillis().toString())
