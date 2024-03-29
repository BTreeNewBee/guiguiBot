package com.iguigui.bot.wechatBot


import com.iguigui.process.service.WechatMessageService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct


@Configuration
open class WechatBotStarter {

    @Autowired
    lateinit var wechatMessageService: WechatMessageService

    @Autowired
    lateinit var wechatBot: WechatBot

    @PostConstruct
    open fun wechatBot() {
        wechatBot.login()
        GlobalScope.launch {
            delay(1000 * 5L)
            wechatBot.loadContactInfo()
        }
    }

}