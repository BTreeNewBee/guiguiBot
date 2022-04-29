package com.iguigui.process.service

import com.iguigui.process.qqbot.dto.BaseResponse
import com.iguigui.common.interfaces.DTO

interface MessageHandler {

    fun handler(message: DTO)

}