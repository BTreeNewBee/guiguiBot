package com.iguigui.process.qqbot

import com.iguigui.process.qqbot.dto.BaseRequest
import com.iguigui.process.qqbot.dto.DTO


/**
 * 消息转换器
 * 接收消息则registerHandler
 * 发送消息则调用sendMessage
 * 可能有多种实现
 */
interface MessageAdapter {

    fun registerHandler(handler: (message: DTO) -> Unit)

    fun sendMessage(message : BaseRequest)

}