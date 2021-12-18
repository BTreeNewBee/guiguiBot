package com.iguigui.qqbot.schedule.daily

import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.service.MessageService
import com.iguigui.qqbot.service.WechatMessageService
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

    @Autowired
    lateinit var wechatMessageService: WechatMessageService

    @Scheduled(cron = "1 0 9 * * 1,2,3,4,5")
//    @Scheduled(cron = "0 */1 * * * ?")
    fun morningNotice() {
        bot.groups.forEach {
            val moleNotice = messageUtil.getMoleNotice()
            it.value.sendTextMessage(moleNotice)
        }
    }


    @Scheduled(cron = "0 0 9 * * 1")
    fun monday() {
        bot.groups.forEach {
            it.value.sendTextMessage("周一周一尼玛个哔")
        }
    }

    @Scheduled(cron = "0 0 9 * * 2,3")
    fun tuesdayAndWednesday() {
        bot.groups.forEach {
            it.value.sendTextMessage("什么时候周五？")
        }
    }

    @Scheduled(cron = "0 0 9 * * 4")
    fun thursday() {
        bot.groups.forEach {
            it.value.sendTextMessage("肯德基疯狂星期四，谁请我吃？")
        }
    }

    @Scheduled(cron = "0 0 9 * * 5")
    fun friday() {
        bot.groups.forEach {
            it.value.sendTextMessage("这个B班，就上到这！")
        }
    }

    @Scheduled(cron = "0 0 9 * * 6")
    fun saturday() {
        bot.groups.forEach {
            it.value.sendTextMessage("睡")
        }
    }

    @Scheduled(cron = "0 0 9 * * 7")
    fun sunday() {
        bot.groups.forEach {
            it.value.sendTextMessage("怎么明天又周一了？")
        }
    }

    @Scheduled(cron = "5 0 0 * * ?")
    fun dailyGroupMessageCount() {
        wechatMessageService.dailyGroupMessageCount()
    }

}