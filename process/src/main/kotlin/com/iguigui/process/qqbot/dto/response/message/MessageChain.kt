package com.iguigui.process.qqbot.dto.response.message

data class MessageChain(
    val base64: Any,
    val id: Int,
    val imageId: String,
    val path: Any,
    val time: Int,
    val type: String,
    val url: String
)