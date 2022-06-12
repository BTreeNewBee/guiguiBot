package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.ComplexSubjectDTO
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.NudgeDTO
import com.iguigui.process.qqbot.dto.NudgeEventDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Nudge {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @SubscribeBotMessage(name = "自动回戳")
    fun nudge(dto: NudgeEventDTO) {
        println(dto)
        if (dto.target == 3633266931 && dto.subject.kind == "Group") {
            messageAdapter.sendGroupMessage(dto.subject.id,NudgeDTO(dto.target,dto.fromId,"Group"))
        }
    }

}