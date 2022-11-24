package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.handler.MessageDispatcher1
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class BotHelper {

    @Autowired
    lateinit var messageDispatcher: MessageDispatcher1

    @SubscribeBotMessage("Bot帮助菜单", "帮助",false)
    fun helper(dto: GroupMessagePacketDTO) {
        messageDispatcher.handlerBeans.forEach { (method, _) ->
            method.getAnnotation(SubscribeBotMessage::class.java).let {
                //今天的特别编码行动就到此结束力
            }
        }
    }

}