package com.iguigui.process.express.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Info(
    @SerialName("com")
    val com: String  = "",
    @SerialName("context")
    val context: List<Context>,
    @SerialName("current")
    val current: String  = "",
    @SerialName("currentStatus")
    val currentStatus: String  = "",
    @SerialName("isAbstract")
    val isAbstract: String  = "",
    @SerialName("latest_progress")
    val latestProgress: String  = "",
    @SerialName("latest_time")
    val latestTime: String  = "",
    @SerialName("send_time")
    val sendTime: String  = "",
    @SerialName("_source_com")
    val sourceCom: String  = "",
    @SerialName("state")
    val state: String  = "",
    @SerialName("status")
    val status: String  = "",
    @SerialName("_support_from")
    val supportFrom: String = ""
)