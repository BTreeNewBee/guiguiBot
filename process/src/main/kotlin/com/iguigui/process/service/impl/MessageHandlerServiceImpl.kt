package com.iguigui.process.service.impl

import com.iguigui.common.service.MessageHandlerService
import com.iguigui.process.qqbot.MessageAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

class MessageHandlerServiceImpl : MessageHandlerService {

    override fun getContent(message: String): String {
        println("handler message $message")
        return   message + "yes!"
    }

}