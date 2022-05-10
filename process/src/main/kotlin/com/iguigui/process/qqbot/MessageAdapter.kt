package com.iguigui.process.qqbot

import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.dto.BaseRequest
import com.iguigui.process.qqbot.dto.MessageDTO
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain


/**
 * 消息转换器
 * 接收消息则registerHandler
 * 发送消息则调用sendMessage
 * 可能有多种实现
 */
interface MessageAdapter {

    fun registerHandler(handler: (message: DTO) -> Unit)

    //公有接口
    fun sendMessage(message: BaseRequest)

    //给群发送文本消息
    fun sendGroupMessage(id: Long, message: String)

    //群发送复杂消息
    fun sendGroupMessage(id: Long, message: MessageDTO)

}