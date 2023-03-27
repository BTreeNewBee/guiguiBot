package com.iguigui.process.schedule.daily

import com.iguigui.common.service.MessageService
import com.iguigui.process.service.Subscriber
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyGroupMessageCount {

    val log = LogFactory.getLog(DailyGroupMessageCount::class.java)!!

    @Autowired
    lateinit var subscriber: Subscriber

    @Scheduled(cron = "0 0 0 * * ?")
    fun dailyGroupMessageCount() {
        subscriber.dailyGroupMessageCount()
    }


}