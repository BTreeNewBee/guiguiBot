package com.iguigui.service.impl

import com.iguigui.common.service.QQBotService
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.OfflineAudio
import net.mamoe.mirai.utils.ExternalResource
import org.apache.dubbo.config.annotation.DubboService
import org.springframework.beans.factory.annotation.Autowired

@DubboService
open class QQBotServiceImpl : QQBotService {

    @Autowired
    private lateinit var bot: Bot

    override fun sendGroupMessage(group: Group, message: String) {
        runBlocking {
            bot.getGroup(group.id)?.sendMessage(message)
        }
    }

    override fun sendGroupMessage(group: Group, message: Message) {
        runBlocking {
            bot.getGroup(group.id)?.sendMessage(message)
        }
    }

    override fun groupUploadAudio(group: Group, externalResource: ExternalResource) : OfflineAudio {
        val offlineAudio : OfflineAudio
        runBlocking {
            offlineAudio = group.uploadAudio(externalResource)
        }
        return offlineAudio
    }


}