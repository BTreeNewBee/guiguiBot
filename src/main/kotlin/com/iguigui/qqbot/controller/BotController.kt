package com.iguigui.qqbot.controller

import net.mamoe.mirai.Bot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BotController {

    @Autowired
    lateinit var bot: Bot

    @GetMapping("/hello")
    fun blog(): String {
        return if(bot.isOnline) bot.nick else "QQ机器人未登录！"
    }

}