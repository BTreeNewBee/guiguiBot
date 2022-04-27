package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.springframework.stereotype.Component

@Component
class Subscriber {


    @SubscribeBotMessage(GroupMessagePacketDTO::class)
    fun groupMessageEvent(dto: GroupMessagePacketDTO) {


    }

}