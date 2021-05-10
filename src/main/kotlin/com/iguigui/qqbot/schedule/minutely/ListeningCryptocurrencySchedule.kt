package com.iguigui.qqbot.schedule.minutely

import com.iguigui.qqbot.service.MessageService
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ListeningCryptocurrencySchedule {

    val log = LogFactory.getLog(ListeningCryptocurrencySchedule::class.java)!!

    @Autowired
    lateinit var messageService: MessageService

    @Scheduled(cron = "0 * * * * ?")
    fun listeningCryptocurrencySchedule() {
        messageService.listeningCryptocurrencySchedule()
    }

}