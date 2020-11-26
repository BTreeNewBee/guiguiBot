package com.iguigui.qqbot.schedule.daily

import com.iguigui.qqbot.service.MessageService
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DailyGroupMessageCount {

    val log = LogFactory.getLog(DailyGroupMessageCount::class.java)!!

    @Autowired
    lateinit var bot: Bot

    @Autowired
    lateinit var messageService: MessageService

    @Scheduled(cron = "5 0 0 * * ?")
    fun dailyGroupMessageCount() {
        messageService.dailyGroupMessageCount()
    }

//    @Scheduled(fixedDelay = 1000L)
//    fun testSchedule() {
//        println("test at ${LocalDateTime.now()}")
//    }

}