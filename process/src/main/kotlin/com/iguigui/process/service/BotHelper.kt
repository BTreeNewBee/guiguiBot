package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.stereotype.Component


@Component
class BotHelper {



    @SubscribeBotMessage("Bot帮助菜单", "帮助",false)
    fun flatterer(dto: GroupMessagePacketDTO) {


    }

}