package com.iguigui.process.service

import com.iguigui.process.qqbot.dto.response.BaseResponse

interface MessageHandler {

    fun handler(message: BaseResponse)

}