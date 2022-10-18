package com.iguigui.process.mq

import com.iguigui.process.qqbot.MessageAdapter
import org.apache.commons.logging.LogFactory
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.amqp.rabbit.core.RabbitTemplate
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

    val log = LogFactory.getLog(MyBandMQListener::class.java)!!

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    lateinit var messageAdapter: MessageAdapter

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
        when (message) {
            "FellAsleep" -> {
                messageAdapter.sendGroupMessage( 984701657, "龟龟特别睡觉行动开始力~")
            }
            "WokeUp" -> {
                messageAdapter.sendGroupMessage(984701657, "龟龟醒了,但是代价是什么呢?")
            }
        }
    }

}