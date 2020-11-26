package com.iguigui.qqbot.schedule.daily

import com.iguigui.qqbot.service.MessageService
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyGroupMessageCount {

    val log = LogFactory.getLog(DailyGroupMessageCount::class.java)!!

    @Autowired
    lateinit var bot: Bot

    @Autowired
    lateinit var messageService: MessageService

    @Scheduled(cron = "0 0 * * * *")
    fun dailyGroupMessageCount() {
        messageService.dailyGroupMessageCount()
    }

}