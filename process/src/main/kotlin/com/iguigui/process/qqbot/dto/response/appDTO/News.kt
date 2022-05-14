package com.iguigui.process.qqbot.dto.response.appDTO


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    @SerialName("action")
    val action: String,
    @SerialName("android_pkg_name")
    val androidPkgName: String,
    @SerialName("app_type")
    val appType: Int,
    @SerialName("appid")
    val appid: Int,
    @SerialName("ctime")
    val ctime: Int,
    @SerialName("desc")
    val desc: String,
    @SerialName("jumpUrl")
    val jumpUrl: String,
    @SerialName("preview")
    val preview: String,
    @SerialName("source_icon")
    val sourceIcon: String,
    @SerialName("source_url")
    val sourceUrl: String,
    @SerialName("tag")
    val tag: String,
    @SerialName("title")
    val title: String,
    @SerialName("uin")
    val uin: Int
)