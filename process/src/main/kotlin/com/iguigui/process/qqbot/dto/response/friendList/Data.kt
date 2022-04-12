package com.iguigui.process.qqbot.dto.response.friendList

data class Data(
    val code: Int,
    val `data`: List<FriendListInfo>,
    val msg: String
)