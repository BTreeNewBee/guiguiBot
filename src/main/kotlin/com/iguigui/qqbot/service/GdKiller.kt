package com.iguigui.qqbot.service

import com.iguigui.qqbot.dto.CclMessage
import net.mamoe.mirai.event.events.GroupMessageEvent


interface GdKiller {

    suspend fun processMessage(event : GroupMessageEvent)

    suspend fun listeningMQMessage(message: CclMessage)

}