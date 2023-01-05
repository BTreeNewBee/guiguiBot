package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dao.MessagesMapper
import com.iguigui.process.imagegenerator.GeneratorService
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.ImageDTO
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

@Component
class MessageCount {


    @Autowired
    lateinit var messageAdapter: MessageAdapter

    val log = LogFactory.getLog(MessageCount::class.java)!!

    @Autowired
    lateinit var groupHasQqUserMapper: GroupHasQqUserMapper

    @Autowired
    lateinit var messagesMapper: MessagesMapper

    @Autowired
    lateinit var generatorService: GeneratorService


    @SubscribeBotMessage(name = "消息统计", export = false)
    fun currentGroupMessageCount(dto: GroupMessagePacketDTO) {
        val group = dto.sender.group
        if (dto.contentToString() == "消息统计") {
            val now = LocalDateTime.now()
            val startTime = now at 0 hour 0 minute 0 second time
            val endTime = now at 23 hour 59 minute 59 second time
            val dailyGroupMessageCount =
                messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
            if (dailyGroupMessageCount.isEmpty()) {
                return
            }
            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
            val now1 = LocalDate.now()

            val rate = now1.dayOfYear / 365.0
            val hue = (120 * (1 - rate)).toInt()
            val color = "hsl($hue, 80%, 45%)"

            val arrayList = ArrayList<RankInfo>()
            dailyGroupMessageCount.forEachIndexed { index, groupMessageCountEntity ->
                val groupHasQqUser =
                    groupHasQqUserMapper.selectByGroupIdAndQqUserId(group.id, groupMessageCountEntity.qqUserId!!)
                groupHasQqUser.let {
                    arrayList.add(RankInfo(index + 1, "", it.nickName ?: "", it.messageCount ?: 0))
                }
            }

            val data = MessageRank(
                now1.year, now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                now1.dayOfYear,
                now1.dayOfYear * 1.0 / now1.lengthOfYear(),
                color,
                messageSum,
                arrayList
            )

            val image = generatorService.generateImage("messageRank", data, screenHeight = 260 + 40 * arrayList.size)

            runBlocking {
                messageAdapter.sendGroupMessage(group.id, ImageDTO(image.absolutePath))
                image.delete()
            }
        }
    }


    object ago
    object time

    infix fun Int.days(ago: ago) = LocalDateTime.now() - Period.ofDays(this)

    data class TimeObject(var localDateTime: LocalDateTime, var time: Int)

    infix fun LocalDateTime.at(number: Int) = TimeObject(this, number)

    infix fun TimeObject.hour(hour: Int): TimeObject {
        this.localDateTime = this.localDateTime.withHour(this.time)
        this.time = hour
        return this
    }

    infix fun TimeObject.minute(minute: Int): TimeObject {
        this.localDateTime = this.localDateTime.withMinute(this.time)
        this.time = minute
        return this
    }

    infix fun TimeObject.second(time: time): LocalDateTime {
        this.localDateTime = this.localDateTime.withSecond(this.time)
        return this.localDateTime
    }


}


data class MessageRank(
    val year: Int,
    val date: String,
    val dayLeft: Int,
    val rate: Double,
    val color: String,
    val messageCount: Int,
    val rankInfos: List<RankInfo>
)


data class RankInfo(
    val rank: Int,
    val avatar: String,
    val nickName: String,
    val messageCount: Int,
)
