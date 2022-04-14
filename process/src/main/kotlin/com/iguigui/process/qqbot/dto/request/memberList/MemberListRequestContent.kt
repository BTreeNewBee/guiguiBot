package com.iguigui.process.qqbot.dto.request.memberList

import com.iguigui.process.qqbot.dto.request.Content
import kotlinx.serialization.Serializable

@Serializable
data class MemberListRequestContent (val target:String) : Content()