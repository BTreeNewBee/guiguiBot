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
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList

@Component
class Flatterer {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    //舔狗语录
    private var flattererInfoList: MutableList<String> = ArrayList()

    //绿茶语录
    private var angelicBitchInfoList: MutableList<String> = ArrayList()

    //丫头文学
    private var girlInfoList: MutableList<String> = ArrayList()

    @PostConstruct
    fun initFlatterer() {
        var find1 = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            FlattererInfo::class.java
        )
        find1.firstOrNull()?.run {
            flattererInfoList = this.data
        }
        var find2 = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            AngelicBitchInfo::class.java
        )
        find2.firstOrNull()?.run {
            angelicBitchInfoList = this.data
        }
        var find3 = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            GirlInfo::class.java
        )
        find3.firstOrNull()?.run {
            girlInfoList = this.data
        }
    }

    @SubscribeBotMessage(name = "舔狗语录")
    fun flatterer(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "舔狗语录") {
            return
        }
        if (flattererInfoList.size == 0) {
            return
        }
        messageAdapter.sendGroupMessage(dto.sender.group.id, flattererInfoList.random())
    }


    @SubscribeBotMessage(name = "绿茶语录")
    fun angelicBitch(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "绿茶语录") {
            return
        }
        if (angelicBitchInfoList.size == 0) {
            return
        }
        messageAdapter.sendGroupMessage(dto.sender.group.id, angelicBitchInfoList.random())
    }

    @SubscribeBotMessage(name = "丫头文学")
    fun girl(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "丫头文学" && contentToString != "油腻语录") {
            return
        }
        if (girlInfoList.size == 0) {
            return
        }
        messageAdapter.sendGroupMessage(dto.sender.group.id, girlInfoList.random())
    }
}