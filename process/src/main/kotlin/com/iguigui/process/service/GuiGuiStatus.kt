package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

//查询龟龟状态
@Component
class GuiGuiStatus {

    val log = org.slf4j.LoggerFactory.getLogger(GuiGuiStatus::class.java)

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    private var json = Json {
        ignoreUnknownKeys = true

    }

    private var currentStatus = StatusEnum.Unknown

    //变更时间
    private var changeTime = LocalDateTime.now()

    @SubscribeBotMessage(functionName = "查询龟龟状态", export = true)
    fun guiGuiStatus(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString == "查询龟龟状态") {
            when (currentStatus) {
                StatusEnum.FellAsleep -> {
                    //计算上次状态变更到现在的时间差
                    val time = LocalDateTime.now().minusHours(changeTime.hour.toLong())
                        .minusMinutes(changeTime.minute.toLong()).minusSeconds(changeTime.second.toLong())
                    messageAdapter.sendGroupMessage(
                        dto.sender.group.id,
                        "龟龟特别睡觉行动已经开始力，已经睡了${time.hour}小时${time.minute}分钟${time.second}秒了！"
                    )
                }

                StatusEnum.WokeUp -> {
                    val time = LocalDateTime.now().minusHours(changeTime.hour.toLong())
                        .minusMinutes(changeTime.minute.toLong()).minusSeconds(changeTime.second.toLong())
                    messageAdapter.sendGroupMessage(
                        dto.sender.group.id,
                        "龟龟已经保持清醒${time.hour}小时${time.minute}分钟${time.second}秒了，可代价是什么呢？"
                    )
                }

                StatusEnum.Unknown -> {
                    messageAdapter.sendGroupMessage(
                        dto.sender.group.id,
                        "龟龟状态未知，可能掉线了，也可能是无了？"
                    )
                }
            }
        }
    }


    fun statusChange(status: String) {
        when (status) {
            "FellAsleep" -> {
                currentStatus = StatusEnum.FellAsleep
                changeTime = LocalDateTime.now()
            }

            "WokeUp" -> {
                currentStatus = StatusEnum.WokeUp
                changeTime = LocalDateTime.now()
            }
        }
    }

    //每个小时检查一次最后更新时间，超过24小时状态置为未知
    @Scheduled(cron = "0 0 * * * ?")
    fun checkStatus() {
        if (LocalDateTime.now().minusHours(24).isAfter(changeTime)) {
            currentStatus = StatusEnum.Unknown
        }
    }


}


enum class StatusEnum {
    FellAsleep, WokeUp, Unknown
}