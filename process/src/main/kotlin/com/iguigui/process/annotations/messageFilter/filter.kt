package com.iguigui.process.annotations.messageFilter

import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO

interface GroupMessageFilter {
    fun filter(groupMessagePacketDTO: GroupMessagePacketDTO): Boolean

}