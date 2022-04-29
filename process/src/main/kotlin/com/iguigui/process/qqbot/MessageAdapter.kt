package com.iguigui.process.qqbot

import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.dto.BaseRequest


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