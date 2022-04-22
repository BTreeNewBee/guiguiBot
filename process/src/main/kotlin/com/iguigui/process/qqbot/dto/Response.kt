package com.iguigui.process.qqbot.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
open class BaseResponse(
    val syncId: String?,
    val command: String,
)

fun BaseResponse.toJson() = Json.encodeToString(this)


@Serializable
data class MemberListResponse(
    @SerialName("data")
    var `data`: MemberListData,
) : BaseResponse("", Paths.memberList)


@Serializable
data class MemberListData(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: List<MemberDTO>,
    @SerialName("msg")
    val msg: String
)


@Serializable
data class GroupListResponse(
    @SerialName("data")
    val `data`: GroupListData,
) : BaseResponse("", Paths.groupList)

@Serializable
data class GroupListData(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: List<GroupDTO>,
    @SerialName("msg")
    val msg: String
)

