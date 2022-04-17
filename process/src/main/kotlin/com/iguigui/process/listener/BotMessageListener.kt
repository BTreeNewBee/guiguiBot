package com.iguigui.qqbot.qqBot

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.iguigui.common.service.MessageService
import com.iguigui.process.qqbot.MessageAdapt
import com.iguigui.process.service.MessageHandler
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MemberCardChangeEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import org.apache.dubbo.config.annotation.DubboReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BotMessageListener {

    @Autowired
    lateinit var messageAdapt: MessageAdapt

    @Autowired
    lateinit var messageHandle: MessageHandler

    @PostConstruct
    fun registerAdapt() {
        messageAdapt.registerHandler {messageHandle}
    }


}