package com.iguigui.process.schedule.minutely

import com.iguigui.common.service.MessageService
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component

@Component
class ListeningCryptocurrencySchedule {

    val log = LogFactory.getLog(ListeningCryptocurrencySchedule::class.java)!!
//
//    @DubboReference
//    lateinit var messageService: MessageService

//    @Scheduled(cron = "0 * * * * ?")
//    fun listeningCryptocurrencySchedule() {
//        messageService.listeningCryptocurrencySchedule()
//    }

}