package com.iguigui.process.service.impl

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject
import com.iguigui.process.dto.CclMessage
import com.iguigui.process.service.GdKiller
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class GdKillerImpl : GdKiller {
//
//    @Autowired
//    lateinit var bot : Bot

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    override suspend fun processMessage(event: GroupMessageEvent) {
        val id = event.sender.id
        val contentToString = event.message.contentToString()
        val cclMessage: JsonObject = jsonObject(
                "id" to id,
                "content" to contentToString,
                "groupId" to event.sender.group.id,
        )
        rabbitTemplate.convertAndSend("botinfo",cclMessage.toString())
    }

    override suspend fun listeningMQMessage(message: CclMessage) {
//        if (message.groupId == 1049125084L) {
//            return
//        }
//        val group = bot.getGroup(message.groupId)
//        if (group == null) {
//            println("group not found message $message")
//            return
//        }
//        if(message.atAction == 1) {
//            val chain = buildMessageChain {
//                +At(message.id)
//                +PlainText(message.content)
//            }
//            group.sendMessage(chain)
//        } else {
//            group.sendMessage(message.content)
//        }
    }


}