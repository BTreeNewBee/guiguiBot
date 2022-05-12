package com.iguigui.process.service

import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.common.interfaces.DTO
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dao.MessagesMapper
import com.iguigui.process.dao.QqGroupMapper
import com.iguigui.process.dao.QqUserMapper
import com.iguigui.process.entity.GroupHasQqUser
import com.iguigui.process.entity.Messages
import com.iguigui.process.entity.QqGroup
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import com.iguigui.process.service.impl.MessageHandlerImpl
import com.iguigui.process.service.impl.MessageServiceImpl
import com.iguigui.process.util.MessageUtil
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Resource

@Component
class Subscriber {


    @Autowired
    lateinit var qqGroupMapper: QqGroupMapper

    @Autowired
    lateinit var qqUserMapper: QqUserMapper

    @Autowired
    lateinit var groupHasQqUserMapper: GroupHasQqUserMapper

    @Autowired
    lateinit var messagesMapper: MessagesMapper

    @Resource
    lateinit var messageUtil: MessageUtil

    @Value("\${macAddress}")
    lateinit var macAddress: String

    @Value("\${baseFilePath}")
    lateinit var baseFilePath: String

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    @SubscribeBotMessage
    fun searchHelper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        searchHelperMap.entries.forEach {
            if (contentToString.lowercase(Locale.getDefault()).startsWith(it.key)) {
                val substring = contentToString.substring(2)
                if (substring.trim().isNotEmpty()) {
                    runBlocking {
                        messageAdapter.sendGroupMessage(
                            dto.sender.group.id,
                            "这也要我教？？？自己去看\n${
                                it.value + URLEncoder.encode(
                                    substring.trim(),
                                    "UTF-8"
                                )
                            }"
                        )
                    }
                }
            }
        }
    }

    //消息撤回事件，进行撤回重发
    @SubscribeBotMessage
    fun groupRecallMessageEvent(dto: GroupRecallEventDTO) {
        val now = LocalDateTime.now();
        val startTime = now.plusMinutes(-5)
        var message =
            messagesMapper.getMessageByMessageId(startTime.toString(), now.toString(), dto.messageId, dto.group.id)
        message?.let {
            messageAdapter.sendGroupMessage(dto.group.id, it.senderName + ":" + it.messageDetail)
        }
    }


    fun dailyGroupMessageCount() {
        val yesterday = 1 days ago
        val startTime = yesterday at 0 hour 0 minute 0 second time
        val endTime = yesterday at 23 hour 59 minute 59 second time
        val selectList = qqGroupMapper.selectList(QueryWrapper())

        for (group in selectList) {
            group.id?.let {
                val dailyGroupMessageCount =
                    messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), it)
                if (dailyGroupMessageCount.isEmpty()) {
                    return@let
                }
                val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), it)
                var index = 1
                val stringBuilder = StringBuilder()
                stringBuilder.append("龙王排行榜\n")

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

                stringBuilder.append(
                    "本群${yesterday.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
                )
                dailyGroupMessageCount.forEach { entity ->
                    val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(it, entity.qqUserId!!)
                    stringBuilder.append("第${index}名：${groupHasQqUser.nameCard} ，${entity.messageCount}条消息\n")
                    index++
                }
                stringBuilder.append("晚安~")
                runBlocking {
                    messageAdapter.sendGroupMessage(it, stringBuilder.toString())
                }
            }
        }
    }


    //进行群成员信息同步
    @SubscribeBotMessage
    fun memberListEvent(dto: MemberListData) {
        dto.list.forEach { this::syncMember }
    }


    //群信息同步
    @SubscribeBotMessage
    fun groupListEvent(dto: GroupListData) {
        dto.list.forEach { this::syncGroup }
    }

    //常规图片下载
    @SubscribeBotMessage
    fun imageDownload(dto: GroupMessagePacketDTO) {
        dto.messageChain.filter { it is ImageDTO }.map { it as ImageDTO }.forEach {
            it.imageId?.let { it1 -> it.url?.let { it2 -> downloadImage(it1, it2) } }
        }
    }

    //闪照事件监听
    @SubscribeBotMessage
    fun flashImageEvent(dto: GroupMessagePacketDTO) {
        dto.messageChain.filter { it is FlashImageDTO }.map { it as FlashImageDTO }.forEach {
            it.imageId?.let { it1 -> it.url?.let { it2 -> downloadImage(it1, it2) } }
            //发现闪照就往群里当作普通图片重发一遍
            messageAdapter.sendGroupMessage(dto.sender.group.id, ImageDTO(url = it.url))
        }
    }

    //图片下载
    private fun downloadImage(imageId: String, url: String) {
        val digestHex = MD5.create().digestHex(imageId)
        val filePath =
            "$baseFilePath/${digestHex[digestHex.length - 2]}${digestHex[digestHex.length - 1]}/${imageId}"
        if (!File(filePath).exists()) {
            HttpUtil.downloadFile(url, filePath)
        }
    }


    //改名提醒
    @SubscribeBotMessage
    fun memberCardChangeEvent(dto: MemberCardChangeEventDTO) {
        messageAdapter.sendGroupMessage(dto.member.group.id, "有人改名字了我不说是谁")
    }

    //每次有消息都同步一下
    @SubscribeBotMessage
    fun groupEvent(dto: GroupMessagePacketDTO) {
        val group = dto.sender.group
        syncGroup(group)
        syncMember(dto.sender)
    }

    //有啥都给存数据库
    @SubscribeBotMessage
    fun messageLogger(dto: GroupMessagePacketDTO) {
        val sender = dto.sender
        var messages = Messages()
        messages.messageType = 1
        messages.senderId = sender.id
        messages.senderName = sender.memberName
        messages.groupId = sender.group.id
        messages.groupName = sender.group.name
        messages.messageDetail = dto.contentToString()
        messages.messageId = dto.sourceId
        messagesMapper.insert(messages)
    }


    @SubscribeBotMessage
    fun currentGroupMessageCount(dto: GroupMessagePacketDTO) {
        val group = dto.sender.group
        if (dto.contentToString() == "实时") {
            val now = LocalDateTime.now()
            val startTime = now at 0 hour 0 minute 0 second time
            val endTime = now at 23 hour 59 minute 59 second time
            val dailyGroupMessageCount =
                messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
            if (dailyGroupMessageCount.isEmpty()) {
                return
            }
            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
            var index = 1
            val stringBuilder = StringBuilder()
            stringBuilder.append("龙王排行榜\n")
            val now1 = LocalDate.now()
            stringBuilder.append(
                "今天是${
                    now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                }日，今年的第${now1.dayOfYear}天，您的${now1.year}年使用进度条：\n"
            )
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
            stringBuilder.append(
                "本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
            )
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(group.id, it.qqUserId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser.nameCard} ，${it.messageCount}条消息\n")
                index++
            }
            runBlocking {
                messageAdapter.sendGroupMessage(group.id, stringBuilder.toString())
            }
        }
    }


    private val searchHelperMap: Map<String, String> = mapOf(
        "百度" to "https://www.baidu.com/baidu?wd=",
        "谷歌" to "https://www.google.com/search?q=",
        "必应" to "https://cn.bing.com/search?q=",
        "淘宝" to "https://s.taobao.com/search?q=",
        "github" to "https://github.com/search?q=",
        "b站" to "https://search.bilibili.com/all?keyword=",
    )


    private fun syncMember(member: MemberDTO) {
        var groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(member.group.id, member.id)
        if (groupHasQqUser == null) {
            groupHasQqUser = GroupHasQqUser()
            groupHasQqUser.groupId = member.group.id
            groupHasQqUser.qqUserId = member.id
            groupHasQqUser.nickName = member.memberName
            groupHasQqUserMapper.insert(groupHasQqUser)
        } else {
            groupHasQqUser.nickName = member.memberName
            groupHasQqUserMapper.updateById(groupHasQqUser)
        }
    }

    private fun syncGroup(group: GroupDTO) {
        var qqGroup = qqGroupMapper.selectById(group.id)
        if (qqGroup == null) {
            qqGroup = QqGroup()
            qqGroup.id = group.id
            qqGroup.name = group.name
            qqGroupMapper.insert(qqGroup)
        } else {
            qqGroup.name = group.name
            qqGroupMapper.updateById(qqGroup)
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

fun GroupMessagePacketDTO.contentToString(): String {
    return this.messageChain.joinToString("") { it.toString() }
}