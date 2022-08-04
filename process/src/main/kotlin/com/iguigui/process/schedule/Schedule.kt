package com.iguigui.process.schedule

import com.iguigui.process.service.Subscriber
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Schedule {

    val log = LogFactory.getLog(Schedule::class.java)!!

    @Autowired
    lateinit var subscriber: Subscriber

    @Scheduled(cron = "0 * * * * ?")
    fun dailyGroupMessageCount() {
        subscriber.sendMessage("测试定时任务")
    }


}