package com.iguigui.process.qqbot

import com.iguigui.process.qqbot.dto.BaseRequest
import com.iguigui.process.qqbot.dto.BaseResponse


interface MessageAdapter {

    fun registerHandler(handler: (message: BaseResponse) -> Unit)

    fun sendMessage(message : BaseRequest)

}