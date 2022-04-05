package com.iguigui.process.service

import com.iguigui.process.dto.CclMessage
import net.mamoe.mirai.event.events.GroupMessageEvent


interface GdKiller {

    suspend fun processMessage(event : GroupMessageEvent)

    suspend fun listeningMQMessage(message: CclMessage)

}