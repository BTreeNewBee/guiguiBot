package com.iguigui.qqbot.service.impl

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import com.iguigui.qqbot.dto.CclMessage
import com.iguigui.qqbot.service.GdKiller
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GdKillerImpl : GdKiller {

    @Autowired
    lateinit var bot : Bot

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    override suspend fun processMessage(event: GroupMessageEvent) {
//        if (event.group.id != 641716978L) {
//            return;
//        }
        val id = event.sender.id
        val contentToString = event.message.contentToString()
        val cclMessage: JsonObject = jsonObject(
                "id" to id,
                "content" to contentToString,
                "groupId" to event.sender.group.id,
        )
        println(cclMessage.toString())
        rabbitTemplate.convertAndSend("botinfo",cclMessage.toString())
    }

    override suspend fun listeningMQMessage(message: CclMessage) {
        val group = bot.getGroup(message.groupId)
        group!!.sendMessage(message.content)
    }


}