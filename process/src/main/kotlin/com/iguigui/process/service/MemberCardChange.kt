package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.MemberCardChangeEventDTO
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Random
import java.util.concurrent.ConcurrentHashMap

@Component
class MemberCardChange {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    private var groupMemberCardChangeCache = ConcurrentHashMap<Long, Int>()

    //改名提醒
    @SubscribeBotMessage(functionName = "改名提醒", export = true, description = "群友群名片修改提醒")
    suspend fun memberCardChangeEvent(dto: MemberCardChangeEventDTO) {
        groupMemberCardChangeCache[dto.member.group.id] = RandomUtils.nextInt(5,10)
        println("群名片变更")
    }

    @SubscribeBotMessage(functionName = "群名片变更")
    suspend fun memberCardChange(dto: GroupMessagePacketDTO) {
        val groupId = dto.sender.group.id
        val count = groupMemberCardChangeCache[groupId]?.minus(1) ?: 0

//        messageAdapter.sendGroupMessage(dto.sender.group.id, "有人改名字了我不说是谁")
    }


}