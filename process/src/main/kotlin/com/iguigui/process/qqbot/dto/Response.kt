package com.iguigui.process.qqbot.dto

import com.iguigui.common.interfaces.DTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.ArrayList

@Serializable
open class BaseResponse(
    val syncId: String?,
    val command: String,
    val data : DTO?
)

fun BaseResponse.toJson() = Json.encodeToString(this)


data class GroupListData(
    val list: List<GroupDTO>,
): DTO

data class MemberListData(
    val list: List<MemberDTO>,
) : DTO
