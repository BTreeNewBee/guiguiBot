package com.iguigui.process.service.impl

import com.iguigui.common.service.BotSendMessageService
import com.iguigui.process.service.TestMessageService
import org.apache.dubbo.config.annotation.DubboReference
import org.springframework.stereotype.Service

@Service
class TestTestMessageServiceImpl : TestMessageService{

    @DubboReference
    lateinit var botSendMessageService: BotSendMessageService

    override fun processMessage(string: String) {
        botSendMessageService.sendMessage(string)
    }


}