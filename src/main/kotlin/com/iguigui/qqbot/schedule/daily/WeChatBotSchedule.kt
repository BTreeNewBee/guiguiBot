package com.iguigui.qqbot.schedule.daily

import cn.hutool.http.HttpUtil
import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.service.MessageService
import com.iguigui.qqbot.service.WechatMessageService
import com.iguigui.qqbot.util.MessageUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    var todayIsWorkDay = true

    @Autowired
    lateinit var bot: WechatBot

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var wechatMessageService: WechatMessageService


    //从接口拉取所有非工作日，判定今天是不是工作日
    @Scheduled(cron = "0 30 0 * * ?")
    fun dailyWorkDayLoad() {
        val now = LocalDate.now()
        val month = now.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val url = "https://api.apihubs.cn/holiday/get?field=date&year=${now.year}&month=${month}&workday=2&order_by=1&size=31"
        val get = HttpUtil.get(url)
        log.info("work day check resp :$get")
        val dataList = Json.parseToJsonElement(get).jsonObject["data"]?.jsonObject?.get("list")?.jsonArray
        val today = now.format(DateTimeFormatter.BASIC_ISO_DATE)
        var todayIsWorkDay = true
        dataList?.forEach {
            val date = it.jsonObject["date"]?.jsonPrimitive.toString()
            //今天在list里面就是在非工作日里面
            if (date == today) {
                todayIsWorkDay = false
                return
            }
        }
        this.todayIsWorkDay = todayIsWorkDay

    }

    @Scheduled(cron = "1 0 9 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    fun morningNotice() {
        if (!todayIsWorkDay) {
            return
        }
        bot.groups.forEach {
            val moleNotice = messageUtil.getMoleNotice()
            it.value.sendTextMessage(moleNotice)
        }
    }

    @Scheduled(cron = "0 30 18 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    fun afternoonNotice() {
        if (!todayIsWorkDay) {
            return
        }
        bot.groups.forEach {
            it.value.sendTextMessage("今天这个B班，就上到这！")
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