package com.iguigui.qqbot.schedule.daily

import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.service.MessageService
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


    @Scheduled(cron = "0 0 9 * * 1,2,3,4,5")
    fun morningNotice() {
        bot.groupList.forEach {
            val stringBuilder = StringBuilder()
            stringBuilder.append("摸鱼小助手提醒您：\n")
            val now1 = LocalDate.now()
            stringBuilder.append("今天是${now1.format(DateTimeFormatter.ISO_LOCAL_DATE)}日，今年的第${now1.dayOfYear}天，剩余${now1.lengthOfYear() - now1.dayOfYear}天，您的${now1.year}年使用进度条：\n")
            val d = now1.dayOfYear * 1.0 / now1.lengthOfYear() / 2.0
            var string = "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░"
            val d1 = (string.length * (0.5 - d)).toInt()
            stringBuilder.append(
                "${string.substring(d1, d1 + 20)} ${
                    String.format(
                        "%.2f",
                        now1.dayOfYear * 100.0 / now1.lengthOfYear()
                    )
                }% \n"
            )
            it.sendTextMessage(stringBuilder.toString())
        }
    }

    @Scheduled(cron = "0 30 18 * * 1,2,3,4,5")
    fun afternoonNotice() {
        bot.groupList.forEach {
            it.sendTextMessage("今天这个B班，就上到这！")
        }
    }


}