package com.iguigui.process.qqbot.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
open class BaseResponse(
    val syncId: String?,
    val command: String,
    val data : DTO?
)

fun BaseResponse.toJson() = Json.encodeToString(this)



//
//open class CommandEventDTO(
//    @SerialName("data")
//    var command: String
//) : EventDTO()
//
//
//
//data class MemberListResponse(
//    @SerialName("data")
//    var `data`: MemberListData,
//) : CommandEventDTO(Paths.memberList)
//
//
//
//data class MemberListData(
//    @SerialName("code")
//    val code: Int,
//    @SerialName("data")
//    val `data`: List<MemberDTO>,
//    @SerialName("msg")
//    val msg: String
//) : CommandEventDTO(Paths.memberList)
//
//
//
//data class GroupListResponse(
//    @SerialName("data")
//    val `data`: GroupListData,
//) : CommandEventDTO(Paths.groupList)
//
//
//data class GroupListData(
//    @SerialName("code")
//    val code: Int,
//    @SerialName("data")
//    val `data`: List<GroupDTO>,
//    @SerialName("msg")
//    val msg: String
//)
