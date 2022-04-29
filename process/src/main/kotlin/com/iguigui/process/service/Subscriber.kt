package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.stereotype.Component

@Component
class Subscriber {


    @SubscribeBotMessage(GroupMessagePacketDTO::class)
    fun groupMessageEvent(dto: GroupMessagePacketDTO) {


    }

}