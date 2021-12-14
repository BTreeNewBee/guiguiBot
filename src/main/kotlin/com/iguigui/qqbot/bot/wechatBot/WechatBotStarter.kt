package com.iguigui.qqbot.bot.wechatBot


import com.iguigui.qqbot.bot.Bot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class WechatBotStarter {

    @Bean
    open fun wechatBot() : Bot {
        val wechatBot = WechatBot()
        wechatBot.login()
        println("wechat login success")
        return wechatBot
    }


}