package com.iguigui.process.mq

import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.service.GuiGuiStatus
import org.apache.commons.logging.LogFactory
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.apache.logging.log4j.kotlin.logger
import javax.annotation.PostConstruct

@Component
@RabbitListener(
    bindings = [QueueBinding(
        value = Queue(value = "clientInfo", durable = "true"),
        exchange = Exchange(value = "amq.direct", type = "direct"),
        key = arrayOf("clientInfo")
    )]
)
class MyBandMQListener {    


    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var guiGuiStatus: GuiGuiStatus

    @RabbitHandler
    fun process(message: String) {
        logger.info("Received: $message")
        guiGuiStatus.statusChange(message)
    }

}