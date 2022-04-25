package com.iguigui.process.service

import com.iguigui.process.qqbot.dto.BaseResponse
import com.iguigui.process.qqbot.dto.DTO

interface MessageHandler {

    fun handler(message: DTO)

}