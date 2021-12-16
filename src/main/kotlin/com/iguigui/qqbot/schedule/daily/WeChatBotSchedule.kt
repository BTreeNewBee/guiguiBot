package com.iguigui.qqbot.schedule.daily

import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.service.MessageService
import com.iguigui.qqbot.util.MessageUtil
import net.mamoe.mirai.Bot
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class WeChatBotSchedule {

    val log = LogFactory.getLog(WeChatBotSchedule::class.java)!!

    @Autowired
    lateinit var bot: WechatBot

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Scheduled(cron = "0 0 9 * * 1,2,3,4,5")
//    @Scheduled(cron = "0 */1 * * * ?")
    fun morningNotice() {
        bot.groupList.forEach {
            val moleNotice = messageUtil.getMoleNotice()
            it.sendTextMessage(moleNotice)
        }
    }

    @Scheduled(cron = "0 30 18 * * 1,2,3,4,5")
//    @Scheduled(cron = "0 */1 * * * ?")
    fun afternoonNotice() {
        bot.groupList.forEach {
            it.sendTextMessage("今天这个B班，就上到这！")
        }
    }


}