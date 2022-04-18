package com.iguigui.qqbot.qqBot

import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.service.MessageHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BotMessageListener {

    @Autowired
    lateinit var messageAdapt: MessageAdapter

    @Autowired
    lateinit var messageHandle: MessageHandler


    @PostConstruct
    fun registerAdapt() {
        messageAdapt.registerHandler {messageHandle}
    }


}