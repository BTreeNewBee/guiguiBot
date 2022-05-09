package com.iguigui.process.qqbot

import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.iguigui.process.service.Subscriber

@Component
class MessageDispatcher : IMessageDispatcher {

    @Autowired
    lateinit var subscriber: Subscriber

    override fun handler(message: DTO) {
        when (message) {
            is GroupMessagePacketDTO -> {
                subscriber.groupMessageEvent(message)
            }
        }
    }

}