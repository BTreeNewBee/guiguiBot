package com.iguigui.process.schedule.daily

import com.iguigui.common.service.MessageService
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.apache.dubbo.config.annotation.DubboReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyGroupMessageCount {

    val log = LogFactory.getLog(DailyGroupMessageCount::class.java)!!

    @DubboReference
    lateinit var messageService: MessageService

    @Scheduled(cron = "5 0 0 * * ?")
    fun dailyGroupMessageCount() {
        messageService.dailyGroupMessageCount()
    }

//    @Scheduled(fixedDelay = 1000L)
//    fun testSchedule() {
//        println("test at ${LocalDateTime.now()}")
//    }

//
//    @Scheduled(cron = "0 6 * * * ?")
//    fun test() {
//        val friend = bot.getFriend(545784329)
//        if (friend != null) {
//            messageService.sendWeather(friend, "深圳")
//        } else {
//            log.info("no such friend")
//        }
//    }

}