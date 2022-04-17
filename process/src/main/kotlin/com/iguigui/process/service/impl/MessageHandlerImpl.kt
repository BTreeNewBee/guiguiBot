package com.iguigui.process.service.impl

import com.iguigui.process.qqbot.dto.response.BaseResponse
import com.iguigui.process.service.MessageHandler
import org.springframework.stereotype.Component

@Component
class MessageHandlerImpl : MessageHandler {

    override fun handler(message: BaseResponse) {
        println(message)
    }


}