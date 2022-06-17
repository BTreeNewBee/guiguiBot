package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.entity.AngelicBitchInfo
import com.iguigui.process.entity.ExpressSubscriberInfo
import com.iguigui.process.entity.FlattererInfo
import com.iguigui.process.entity.GirlInfo
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class Repeater {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    private val preMessage = ConcurrentHashMap<Long, String>()
    private val preMessageCount = ConcurrentHashMap<Long, AtomicInteger>()


    @SubscribeBotMessage(name = "自动复读")
    fun repeater(dto: GroupMessagePacketDTO) {
        val id = dto.sender.group.id
        if (!isRepeatMessage(dto)) {
            return
        }
        val count = preMessageCount.getOrPut(dto.sender.group.id) { AtomicInteger(1) }


    }

    fun isRepeatMessage(dto: GroupMessagePacketDTO): Boolean =
        preMessage[dto.sender.group.id]?.let { return@let it == dto.contentToString() } ?: false

}