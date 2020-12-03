package com.iguigui.qqbot.bot

import com.iguigui.qqbot.service.MessageService
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribe
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BotMessageListener {

    @Autowired
    lateinit var bot : Bot


    @Autowired
    lateinit var messageService: MessageService

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
        bot.subscribeAlways<GroupMessageEvent> {
            messageService.processMessage(this)
        }
        bot.subscribeAlways<MessageRecallEvent.GroupRecall> {
            messageService.processCancelMessage(this)
        }
        bot.subscribeAlways<FriendMessageEvent> {
            messageService.processFriendMessage(this)
        }
        messageService.processGroups(bot.groups)


    }


}