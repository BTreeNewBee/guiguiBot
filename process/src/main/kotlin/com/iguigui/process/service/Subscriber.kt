package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Subscriber {

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    @SubscribeBotMessage
    fun groupMessageEvent(dto: GroupMessagePacketDTO) {
        println("Group MessagePackage DTO = ${dto}")
        messageAdapter.sendMessage(MemberListRequest("1017809249"))
    }

    @SubscribeBotMessage
    fun memberListEvent(dto:MemberListData) {
        println("memberListEvent DTO = ${dto}")
    }

}