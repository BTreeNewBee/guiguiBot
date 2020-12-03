package com.iguigui.qqbot.service.impl

import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.iguigui.qqbot.dao.GroupHasQqUserMapper
import com.iguigui.qqbot.dao.QqGroupMapper
import com.iguigui.qqbot.dao.MessagesMapper
import com.iguigui.qqbot.dao.QqUserMapper
import com.iguigui.qqbot.entity.QqGroup
import com.iguigui.qqbot.entity.GroupHasQqUser
import com.iguigui.qqbot.entity.Messages
import com.iguigui.qqbot.entity.QqUser
import com.iguigui.qqbot.service.MessageService
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.content
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URLEncoder
import java.sql.Wrapper
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

@Service
class MessageServiceImpl : MessageService {


    @Autowired
    lateinit var qqGroupMapper: QqGroupMapper

    @Autowired
    lateinit var qqUserMapper: QqUserMapper

    @Autowired
    lateinit var groupHasQqUserMapper: GroupHasQqUserMapper

    @Autowired
    lateinit var messagesMapper: MessagesMapper

    @Autowired
    lateinit var bot: Bot

    @Transactional
    override fun processMessage(event: GroupMessageEvent) {
        println("message process")
        runBlocking {
            val groupOptional = qqGroupMapper.selectById(event.group.id)
            if (groupOptional == null) {
                syncGroup(event.group)
            }
            val qqUserOptional = qqUserMapper.selectById(event.sender.id)
            if (qqUserOptional == null) {
                syncMember(event.group, event.sender)
            }
            processMessageChain(event.sender, event.message, event.source.id)
        }

    }

    override fun processCancelMessage(event: MessageRecallEvent.GroupRecall) {
        println("cancelMessage process")
        runBlocking {
            sendCancelMessage(event.messageId, event.group)
        }
    }

    private suspend fun sendCancelMessage(id: Int, group: Group) {
        val now = LocalDateTime.now();
        val startTime = now.plusMinutes(-5)
        var message = messagesMapper.getMessageByMessageId(startTime.toString(), now.toString(), id, group.id)
        if (message != null) {
            group.sendMessage(message.senderName + ":" + message.messageDetail)
        }
    }

    private suspend fun processMessageChain(sender: Member, message: MessageChain, id: Int) {
        val contentToString = message.contentToString()
        println("message chain contentToString = $contentToString")
        var messages = Messages()
        messages.messageType = 1
        messages.senderId = sender.id
        messages.senderName = sender.nameCardOrNick
        messages.groupId = sender.group.id
        messages.groupName = sender.group.name
        messages.messageDetail = contentToString
        messages.messageId = id
        messagesMapper.insert(messages)

        if (contentToString == "/查询实时记录") {
            currentGroupMessageCount();
        }

        if (contentToString.startsWith("百度")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage("怎么百度也要我教？？？自己去看：https://www.baidu.com/baidu?wd=" + URLEncoder.encode(substring,"UTF-8"))
            }
        }

    }

    @Transactional
    override fun processGroups(groups: ContactList<Group>) {
        runBlocking {
            groups.forEach { group ->
                syncGroup(group)
            }
        }
    }


    override fun dailyGroupMessageCount() {
        val yesterday = 1 days ago
        val startTime = yesterday at 0 hour 0 minute 0 second time
        val endTime = yesterday at 23 hour 59 minute 59 second time
        for (group in bot.groups) {
            val dailyGroupMessageCount = messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
            if (dailyGroupMessageCount.isEmpty()) {
                continue
            }
            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
            var index = 1
            val stringBuilder = StringBuilder()
            stringBuilder.append("龙王排行榜\n")
            stringBuilder.append("本群${yesterday.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日全天消息总量：${messageSum}条\n")
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(1001342116, it.qqUserId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser.nameCard} ，当日消息${it.messageCount}条\n")
                index++
            }
            runBlocking {
                group.sendMessage(stringBuilder.toString())
            }
        }
    }


    fun currentGroupMessageCount() {
        val now = LocalDateTime.now()
        val startTime = now at 0 hour 0 minute 0 second time
        val endTime = now at 23 hour 59 minute 59 second time
        for (group in bot.groups) {
            val dailyGroupMessageCount = messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
            if (dailyGroupMessageCount.isEmpty()) {
                continue
            }
            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
            var index = 1
            val stringBuilder = StringBuilder()
            stringBuilder.append("龙王排行榜\n")
            stringBuilder.append("本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日全天消息总量：${messageSum}条\n")
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(1001342116, it.qqUserId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser.nameCard} ，当日消息${it.messageCount}条\n")
                index++
            }
            runBlocking {
                group.sendMessage(stringBuilder.toString())
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


    override fun processFriendMessage(friendMessageEvent: FriendMessageEvent) {
        if (friendMessageEvent.sender.id == 1479712749L) {
            try {
                dailyGroupMessageCount()
            } catch (e: Exception) {
                println(e)
            }
        }
    }


    private suspend fun syncGroup(group: Group) {
        var qqGroup = qqGroupMapper.selectById(group.id)
        if (qqGroup == null) {
            qqGroup = QqGroup()
            qqGroup.id = group.id
            qqGroup.name = group.name
            qqGroup.userCount = group.members.size
            qqGroupMapper.insert(qqGroup)
        } else {
            qqGroup.name = group.name
            qqGroup.userCount = group.members.size
            qqGroupMapper.updateById(qqGroup)
        }
    }

    private suspend fun syncMember(group: Group, member: Member) {
        var qqUser = qqUserMapper.selectById(member.id)
        if (qqUser == null) {
            qqUser = QqUser()
            qqUser.id = member.id
            qqUser.nickName = member.nick
            qqUserMapper.insert(qqUser)
        } else {
            qqUser.nickName = member.nick
            qqUserMapper.updateById(qqUser)
        }

        var groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(group.id, member.id)
        if (groupHasQqUser == null) {
            groupHasQqUser = GroupHasQqUser()
            groupHasQqUser.groupId = member.group.id
            groupHasQqUser.qqUserId = member.id
            groupHasQqUser.nickName = if (member.nick.isBlank()) member.id.toString() else member.nick
            groupHasQqUser.nameCard = if (member.nameCard.isBlank()) groupHasQqUser.nickName else member.nameCard
            groupHasQqUserMapper.insert(groupHasQqUser)
        } else {
            groupHasQqUser.nickName = if (member.nick.isBlank()) member.id.toString() else member.nick
            groupHasQqUser.nameCard = if (member.nameCard.isBlank()) groupHasQqUser.nickName else member.nameCard
            groupHasQqUserMapper.updateById(groupHasQqUser)
        }
    }


}