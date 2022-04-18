package com.iguigui.process.service.impl

import com.iguigui.process.service.TestMessageService
import org.springframework.stereotype.Service

@Service
class TestTestMessageServiceImpl : TestMessageService{

//    @DubboReference
//    lateinit var botSendMessageService: BotSendMessageService

    override fun processMessage(string: String) {
//        botSendMessageService.sendMessage(string)
    }


}