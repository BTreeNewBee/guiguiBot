package com.iguigui.process.qqbot.dto.response.appDTO


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Extra(
    @SerialName("app_type")
    val appType: Int,
    @SerialName("appid")
    val appid: Int,
    @SerialName("msg_seq")
    val msgSeq: Long,
    @SerialName("uin")
    val uin: Int
)