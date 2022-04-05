package com.iguigui.common.service

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.OfflineAudio
import net.mamoe.mirai.utils.ExternalResource

interface QQBotService {

    fun sendGroupMessage(group: Group, message: String)

    fun sendGroupMessage(group: Group, message: Message)

    fun groupUploadAudio(group: Group, externalResource: ExternalResource) : OfflineAudio

}