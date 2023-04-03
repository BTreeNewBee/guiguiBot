package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.handler.MessageDispatcher1
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import io.ktor.util.collections.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct


@Component
class BotHelper {

    @Autowired
    lateinit var messageDispatcher: MessageDispatcher1

    lateinit var methodList: List<SubscribeBotMessage>

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    val helpCache: MutableMap<Long, Any> = ConcurrentHashMap()

    @PostConstruct
    fun initMethodCache() {
        methodList =
            messageDispatcher.handlerBeans.map { it.key.getAnnotation(SubscribeBotMessage::class.java) }.toList()
    }

    @SubscribeBotMessage("Bot帮助菜单", "帮助", false)
    fun helper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "帮助"
            && contentToString != "菜单"
        ) {
            return
        }
        helpCache[dto.sender.id] = Any()

        var message = messageDispatcher.handlerBeans
            .map { it.key.getAnnotation(SubscribeBotMessage::class.java) }
            .filter { it.export }
            .mapIndexed { index, subscribeBotMessage -> "${index + 1}. ${subscribeBotMessage.name}" }
            .joinToString { "\n" }

        message = "请输入序号选择功能详情：\n$message"

        messageAdapter.sendGroupMessage(dto.sender.group.id, message)
    }


    @SubscribeBotMessage("Bot帮助菜单", "帮助", false)
    fun help0(dto: GroupMessagePacketDTO) {
        if (helpCache[dto.sender.id] == null) {
            return
        }
        val id: Int?
        try {
            id = dto.contentToString().toInt()
        } catch (e: NumberFormatException) {
            return
        }
        helpCache.remove(dto.sender.id)
        messageAdapter.sendGroupMessage(dto.sender.group.id, methodList[id].description)
    }


}