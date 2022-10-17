package com.iguigui.process.mq

import com.iguigui.bot.wechatBot.WechatBot
import org.apache.commons.logging.LogFactory
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import javax.annotation.PostConstruct
import kotlin.math.log

@Component
@RabbitListener(
    bindings = [QueueBinding(
        value = Queue(value = "clientInfo", durable = "true"),
        exchange = Exchange(value = "amq.direct", type = "direct"),
        key = arrayOf("clientInfo")
    )]
)
class MQListener {

    val log = LogFactory.getLog(MQListener::class.java)!!

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @PostConstruct
    fun init() {
        rabbitTemplate.convertAndSend("amq.direct", "serverInfo", "Hello from server!")
    }

//    @Scheduled(fixedDelay = 1000 * 10)
//    fun send() {
//        rabbitTemplate.convertAndSend("amq.direct", "serverInfo", "Hello from server!")
//    }

    @RabbitHandler
    fun process(message: String) {
        log.info("Received: $message")
    }

}