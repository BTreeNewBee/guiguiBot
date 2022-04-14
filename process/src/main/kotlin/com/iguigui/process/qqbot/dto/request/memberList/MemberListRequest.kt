package com.iguigui.process.qqbot.dto.request.memberList

import com.iguigui.process.qqbot.dto.request.BaseRequest
import kotlinx.serialization.Serializable


@Serializable
data class MemberListRequest (val target: String) : BaseRequest(
    command = "memberList",
    content = MemberListRequestContent(target),
    subCommand = "",
    syncId = 123
)
