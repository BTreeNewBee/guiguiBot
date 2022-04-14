package com.iguigui.qqbot.qqBot

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.iguigui.common.service.MessageService
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
    lateinit var bot : Bot


//    @Autowired
//    lateinit var messageService: MessageService

    @DubboReference
    lateinit var messageService: MessageService
//
//    @Autowired
//    lateinit var gdKiller: GdKiller

    @PostConstruct
    fun listenerBotMessage() {
        // 在当前协程作用域 (CoroutineScope) 下创建一个子 Job, 监听一个事件.
        //
        // 手动处理消息
        //
        // subscribeAlways 函数返回 Listener, Listener 是一个 CompletableJob.
        //
        // 例如:
        // ```kotlin
        // runBlocking {// this: CoroutineScope
        //     subscribeAlways<FriendMessage> {
        //     }
        // }
        // ```
        // 则这个 `runBlocking` 永远不会结束, 因为 `subscribeAlways` 在 `runBlocking` 的 `CoroutineScope` 下创建了一个 Job.
        // 正确的用法为:
        // 在 Bot 的 CoroutineScope 下创建一个监听事件的 Job, 则这个子 Job 会在 Bot 离线后自动完成 (complete).
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            messageService.processMessage(this)
        }
//        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
//            gdKiller.processMessage(this)
//        }
        GlobalEventChannel.subscribeAlways<MessageRecallEvent.GroupRecall> {
            messageService.processCancelMessage(this)
        }
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> {
            messageService.processFriendMessage(this)
        }
        GlobalEventChannel.subscribeAlways<MemberCardChangeEvent> {
            messageService.processMemberCardChangeEvent(this)
        }
//        messageService.processGroups(bot.groups)


    }


}