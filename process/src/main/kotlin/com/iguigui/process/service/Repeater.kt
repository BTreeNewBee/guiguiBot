package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


//复读机
@Component
class Repeater {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    private var preMessage = ConcurrentHashMap<Long, String>()

    private var preMessageCount = ConcurrentHashMap<Long, String>()

    @SubscribeBotMessage(name = "自动复读")
    fun girl(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
    }

    fun isRepeatMessage(dto: GroupMessagePacketDTO) =
        preMessage[dto.sender.group.id]?.let { it == dto.contentToString() } ?: false

}