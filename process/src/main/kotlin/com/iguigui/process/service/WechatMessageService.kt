package com.iguigui.process.service

interface WechatMessageService {

    fun processMessage(message: String)

    fun dailyGroupMessageCount()

}