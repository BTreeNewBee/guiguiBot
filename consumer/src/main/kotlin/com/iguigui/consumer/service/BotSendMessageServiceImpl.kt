package com.iguigui.consumer.service

import com.iguigui.common.service.BotSendMessageService
import com.iguigui.common.service.MessageHandlerService
import org.apache.dubbo.config.annotation.DubboService

@DubboService
class BotSendMessageServiceImpl : BotSendMessageService {

    override fun sendMessage(message: String): String {
        println("consumer message $message")
//        val content = messageHandlerService.getContent(message)
//        println("handler return message $content")
//        return content
        return message + "OK"
    }

}