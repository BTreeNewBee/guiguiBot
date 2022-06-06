package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.entity.AngelicBitchInfo
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
class AngelicBitch {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    private var angelicBitchInfoList: MutableList<String> = ArrayList()

    @PostConstruct
    fun initInfo() {
        var find = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            AngelicBitchInfo::class.java
        )
        find.firstOrNull()?.run {
            angelicBitchInfoList = this.data
        }
    }

    @SubscribeBotMessage(name = "绿茶语录")
    fun searchHelper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "绿茶语录") {
            return
        }
        if (angelicBitchInfoList.size == 0) {
            return
        }
        messageAdapter.sendGroupMessage(dto.sender.group.id, angelicBitchInfoList.random())
    }

}