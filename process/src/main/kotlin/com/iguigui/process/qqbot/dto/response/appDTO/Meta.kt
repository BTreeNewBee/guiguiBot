package com.iguigui.process.qqbot.dto.response.appDTO


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("news")
    val news: News
)