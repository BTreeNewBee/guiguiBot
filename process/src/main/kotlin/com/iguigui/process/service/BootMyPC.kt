package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.dto.ComplexSubjectDTO
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.NudgeDTO
import com.iguigui.process.qqbot.dto.NudgeEventDTO
import org.springframework.stereotype.Component


@Component
class BootMyPC {

    @SubscribeBotMessage(name = "开机小助手")
    fun flatterer(dto: GroupMessagePacketDTO) {
        if (dto.contentToString() == "开机") {
            //爷还没想好怎么写
        }

    }

}