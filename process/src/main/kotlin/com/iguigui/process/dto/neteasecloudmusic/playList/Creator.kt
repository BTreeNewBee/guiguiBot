package com.iguigui.process.dto.neteasecloudmusic.playList


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Creator(
    @SerialName("accountStatus")
    val accountStatus: Int,
    @SerialName("anchor")
    val anchor: Boolean,
    @SerialName("authStatus")
    val authStatus: Int,
    @SerialName("authenticationTypes")
    val authenticationTypes: Int,
    @SerialName("authority")
    val authority: Int,
    @SerialName("avatarImgId")
    val avatarImgId: Long,
    @SerialName("avatarImgIdStr")
    val avatarImgIdStr: String,
    @SerialName("avatarUrl")
    val avatarUrl: String,
    @SerialName("backgroundImgId")
    val backgroundImgId: Long,
    @SerialName("backgroundImgIdStr")
    val backgroundImgIdStr: String,
    @SerialName("backgroundUrl")
    val backgroundUrl: String,
    @SerialName("birthday")
    val birthday: Int,
    @SerialName("city")
    val city: Int,
    @SerialName("defaultAvatar")
    val defaultAvatar: Boolean,
    @SerialName("description")
    val description: String,
    @SerialName("detailDescription")
    val detailDescription: String,
    @SerialName("djStatus")
    val djStatus: Int,
    @SerialName("followed")
    val followed: Boolean,
    @SerialName("gender")
    val gender: Int,
    @SerialName("mutual")
    val mutual: Boolean,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("province")
    val province: Int,
    @SerialName("signature")
    val signature: String,
    @SerialName("userId")
    val userId: Int,
    @SerialName("userType")
    val userType: Int,
    @SerialName("vipType")
    val vipType: Int
)