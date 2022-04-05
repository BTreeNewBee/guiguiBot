package com.iguigui.process.mq

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.iguigui.process.dto.CclMessage
import com.iguigui.process.service.GdKiller
import kotlinx.coroutines.runBlocking
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@RabbitListener(queues = ["exchangeinfo"], concurrency = "1")
class MQListener {

    @Autowired
    lateinit var gdKiller: GdKiller

    @RabbitHandler
    fun process(message: String) {
        val gson = Gson()
        val message = gson.fromJson<CclMessage>(message)
        runBlocking { gdKiller.listeningMQMessage(message) }
    }

}