package com.iguigui.process.qqbot.dto

import kotlinx.serialization.Serializable

@Serializable
data class BaseCommandMessage(
    val command: String,
    val content: Content,
    val subCommand: String,
    val syncId: Int
)