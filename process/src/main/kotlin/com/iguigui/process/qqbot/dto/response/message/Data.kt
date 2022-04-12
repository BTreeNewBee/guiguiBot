package com.iguigui.process.qqbot.dto.response.message

data class Data(
    val messageChain: List<MessageChain>,
    val sender: Sender,
    val type: String
)