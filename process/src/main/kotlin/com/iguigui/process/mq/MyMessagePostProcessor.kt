package com.iguigui.process.mq

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessagePostProcessor


object MyMessagePostProcessor: MessagePostProcessor {

    override fun postProcessMessage(message: Message): Message {
        message.messageProperties.apply {
            contentType = "application/json"
            contentEncoding = "UTF-8"
        }
        return message
    }
}