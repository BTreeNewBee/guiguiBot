package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.entity.ExpressSubscriberInfo
import com.iguigui.process.entity.FlattererInfo
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
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList

@Component
class Flatterer {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    private var flattererInfoList: MutableList<String> = ArrayList()

    @PostConstruct
    fun initInfo() {
        var find = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            FlattererInfo::class.java
        )
        find.firstOrNull()?.run {
            flattererInfoList = this.data
        }
    }

    @SubscribeBotMessage(name = "舔狗语录")
    fun searchHelper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "舔狗语录") {
            return
        }
        if (flattererInfoList.size == 0) {
            return
        }
        messageAdapter.sendGroupMessage(dto.sender.group.id, flattererInfoList.random())
    }

}