package com.iguigui.process.schedule.hourly

import com.iguigui.process.service.Subscriber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HourlySchedule {

    @Autowired
    lateinit var subscriber: Subscriber

    @Scheduled(initialDelay = 3600 * 1000, fixedDelay = 3600 * 1000)
    fun scanExpressInfo() {
        subscriber.expressScan()
    }

}