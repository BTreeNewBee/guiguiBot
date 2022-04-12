package com.iguigui.process.qqbot.dto.response.groupList

data class GroupList(
    val code: Int,
    val `data`: List<GroupListInfo>,
    val msg: String
)