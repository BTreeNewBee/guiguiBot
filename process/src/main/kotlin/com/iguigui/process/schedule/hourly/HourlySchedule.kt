package com.iguigui.process.schedule.hourly

import com.iguigui.process.service.Subscriber
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HourlySchedule {

    @Autowired
    lateinit var subscriber: Subscriber

    @Scheduled(initialDelay = 60 * 1000 * 40 , fixedDelay =  60 * 1000 * 30)
    fun scanExpressInfo() {
        subscriber.expressScan()
    }

}