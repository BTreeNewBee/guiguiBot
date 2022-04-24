package com.iguigui.process.qqbot

import com.iguigui.process.qqbot.dto.BaseRequest
import com.iguigui.process.qqbot.dto.BaseResponse
import com.iguigui.process.qqbot.dto.DTO


interface MessageAdapter {

    fun registerHandler(handler: (message: DTO) -> Unit)

    fun sendMessage(message : BaseRequest)

}