package com.iguigui.qqbot.service.impl

import com.iguigui.qqbot.bot.wechatBot.Constant
import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.bot.wechatBot.WechatGroupBO
import com.iguigui.qqbot.bot.wechatBot.dto.*
import com.iguigui.qqbot.dao.WechatGroupHasWechatUserMapper
import com.iguigui.qqbot.dao.WechatGroupMapper
import com.iguigui.qqbot.dao.WechatMessagesMapper
import com.iguigui.qqbot.dao.WechatUserMapper
import com.iguigui.qqbot.entity.WechatGroup
import com.iguigui.qqbot.entity.WechatGroupHasWechatUser
import com.iguigui.qqbot.entity.WechatMessages
import com.iguigui.qqbot.entity.WechatUser
import com.iguigui.qqbot.service.WechatMessageService
import com.iguigui.qqbot.util.MessageUtil
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

@Service
class WechatMessageServiceImpl : WechatMessageService {

    val log = LogFactory.getLog(WechatMessageServiceImpl::class.java)!!

    @Autowired
    lateinit var wechatBot: WechatBot

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var wechatGroupMapper: WechatGroupMapper

    @Autowired
    lateinit var wechatMessagesMapper: WechatMessagesMapper

    @Autowired
    lateinit var wechatUserMapper: WechatUserMapper

    @Autowired
    lateinit var wechatGroupHasWechatUserMapper: WechatGroupHasWechatUserMapper


    override fun processMessage(message: String) {
        if (message == "null") {
            return
        }
        val parseToJsonElement = Json.parseToJsonElement(message)
        val type = parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int
        if (type != Constant.HEART_BEAT) {
            log.info(parseToJsonElement)
        }
        when (parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int) {
            Constant.HEART_BEAT -> {
                log.info("HEART_BEAT")
            }
            Constant.GET_USER_LIST_SUCCSESS -> {
                log.info("GET_USER_LIST_SUCCSESS")
            }
            Constant.USER_LIST -> {
                val groupMap = wechatGroupMapper.selectList(null).associateBy { it.wxId }
                val userMap = wechatUserMapper.selectList(null).associateBy { it.wxId }
                val userListDTO = Json.decodeFromJsonElement<UserListDTO>(parseToJsonElement)
                userListDTO.content?.forEach { content ->
                    content.wxid?.let { wxid ->
                        if (wxid.endsWith("@chatroom")) {
                            val wechatGroup = WechatGroupBO()
                            wechatGroup.setId(wxid)
                            wechatGroup.setWechat(wechatBot)
                            wechatBot.addGroup(wechatGroup)
                            if (!groupMap.containsKey(wxid)) {
                                val wechatGroup1 = WechatGroup()
                                wechatGroup1.wxId = wxid
                                wechatGroup1.name = content.name
                                wechatGroupMapper.insert(wechatGroup1)
                            }
                        } else {
                            if (!userMap.containsKey(wxid)) {
                                val wechatUser = WechatUser()
                                wechatUser.wxId = wxid
                                wechatUser.nickName = content.name
                                wechatUserMapper.insert(wechatUser)
                            }
                        }
                    }
                }
            }
            Constant.RECV_PIC_MSG -> {
                log.info("RECV_PIC_MSG")
                val recverImgMessageDTO = Json.decodeFromJsonElement<RecverImgMessageDTO>(parseToJsonElement)
                val content = recverImgMessageDTO.content
                val endsWith = content?.id1?.endsWith("@chatroom")
                endsWith?.let {
                    if (it) {
                        content.id1?.let { groupWxId ->
                            content.id2?.let { userWxId ->
                                var selectOne =
                                    wechatGroupHasWechatUserMapper.selectByGroupIdAndUserId(groupWxId, userWxId)
                                if (selectOne == null) {
                                    sendSyncGroupMember(groupWxId)
                                }
                                val wechatMessages = WechatMessages()
                                wechatMessages.groupName = ""
                                wechatMessages.messageType = 1
                                wechatMessages.groupWxId = groupWxId
                                wechatMessages.senderWxId = userWxId
                                wechatMessages.senderName = selectOne?.nickName
                                wechatMessages.messageDetail = content.thumb
                                wechatMessagesMapper.insert(wechatMessages)
                            }
                        }
                    }
                }
            }
            Constant.RECV_TXT_MSG -> {
                log.info("RECV_TXT_MSG")
                val recverTextMessageDTO = Json.decodeFromJsonElement<RecverTextMessageDTO>(parseToJsonElement)
                recverTextMessageDTO.content?.contains("摸鱼")?.let { mole ->
                    if (mole) {
                        recverTextMessageDTO.wxid?.let {
                            wechatBot.getGroupById(it)?.sendTextMessage(messageUtil.getMoleNotice())
                        }
                    }
                }

                recverTextMessageDTO.content?.equals("排名")?.let { rank ->
                    if (rank) {
                        recverTextMessageDTO.wxid?.let { groupWxId ->
                            val now = LocalDateTime.now()
                            val startTime = now at 0 hour 0 minute 0 second time
                            val endTime = now at 23 hour 59 minute 59 second time
                            val dailyGroupMessageCount =
                                wechatMessagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), groupWxId)
                            if (dailyGroupMessageCount.isEmpty()) {
                                return
                            }
                            val messageSum = wechatMessagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), groupWxId)
                            var index = 1
                            val stringBuilder = StringBuilder()
                            stringBuilder.append("摸鱼排行榜\n")
                            stringBuilder.append(
                                "本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
                            )
                            dailyGroupMessageCount.forEach {
                                val groupHasQqUser = wechatGroupHasWechatUserMapper.selectByGroupIdAndUserId(groupWxId, it.userWxId!!)
                                stringBuilder.append("第${index}名：${groupHasQqUser?.nickName} ，${it.messageCount}条消息\n")
                                index++
                            }
                            wechatBot.getGroupById(groupWxId)?.sendTextMessage(stringBuilder.toString())
                        }
                    }
                }



                val endsWith = recverTextMessageDTO.wxid?.endsWith("@chatroom")
                endsWith?.let {
                    if (it) {
                        recverTextMessageDTO.wxid?.let { groupWxId ->
                            recverTextMessageDTO.id1?.let { userWxId ->
                                var selectOne =
                                    wechatGroupHasWechatUserMapper.selectByGroupIdAndUserId(groupWxId, userWxId)
                                if (selectOne == null) {
                                    sendSyncGroupMember(groupWxId)
                                }
                                val wechatMessages = WechatMessages()
                                wechatMessages.groupName = ""
                                wechatMessages.messageType = 1
                                wechatMessages.groupWxId = groupWxId
                                wechatMessages.senderWxId = userWxId
                                wechatMessages.senderName = selectOne?.nickName
                                wechatMessages.messageDetail = recverTextMessageDTO.content
                                wechatMessagesMapper.insert(wechatMessages)
                            }
                        }
                    }
                }
            }
            Constant.RECV_XML_MSG -> {
                log.info("RECV_XML_MSG")
            }
            Constant.GET_USER_LIST_FAIL -> {
                log.info("GET_USER_LIST_FAIL")
            }
            Constant.CHATROOM_MEMBER -> {
                log.info("CHATROOM_MEMBER")
                parseToJsonElement.jsonObject["content"]?.jsonArray?.forEach {
                    val chatRoomMemberDTO = Json.decodeFromJsonElement<ChatRoomMemberDTO>(it)
                    val groupWxId = chatRoomMemberDTO.room_id
                    var userMap =
                        wechatGroupHasWechatUserMapper.selectByGroupId(groupWxId).associateBy { it.wechatUserWxId }
                    chatRoomMemberDTO.member.forEach {
                        if (!userMap.containsKey(it)) {
                            var wechatGroupHasWechatUser = WechatGroupHasWechatUser()
                            wechatGroupHasWechatUser.wechatGroupWxId = groupWxId
                            wechatGroupHasWechatUser.wechatUserWxId = it
                            wechatGroupHasWechatUser.nickName = ""
                            wechatGroupHasWechatUserMapper.insert(wechatGroupHasWechatUser)
                            val group = wechatBot.getGroupById(groupWxId)
                            group?.syncGroupMemberNick(it)
                        }
                    }
                }

            }
            Constant.CHATROOM_MEMBER_NICK -> {
                log.info("CHATROOM_MEMBER_NICK")
                val content = parseToJsonElement.jsonObject["content"]?.jsonPrimitive?.content
                content?.let {
                    val chatRoomMemberNickDTO =
                        Json.decodeFromJsonElement<ChatRoomMemberNickDTO>(Json.parseToJsonElement(it))
                    val selectByGroupIdAndUserId = wechatGroupHasWechatUserMapper.selectByGroupIdAndUserId(
                        chatRoomMemberNickDTO.roomid,
                        chatRoomMemberNickDTO.wxid
                    )
                    selectByGroupIdAndUserId?.let {
                        it.nickName = chatRoomMemberNickDTO.nick
                        wechatGroupHasWechatUserMapper.updateById(it)
                    }
                }
            }
            Constant.PERSONAL_INFO -> {
                log.info("PERSONAL_INFO")
            }
            Constant.DEBUG_SWITCH -> {
                log.info("DEBUG_SWITCH")
            }
            Constant.PERSONAL_DETAIL -> {
                log.info("PERSONAL_DETAIL")
            }
            Constant.DESTROY_ALL -> {
                log.info("DESTROY_ALL")
            }
            Constant.NEW_FRIEND_REQUEST -> {
                log.info("NEW_FRIEND_REQUEST")
            }
            Constant.AGREE_TO_FRIEND_REQUEST -> {
                wechatBot.loadContactInfo()
            }

        }

    }

    override fun dailyGroupMessageCount() {
        wechatBot.groups.forEach {
            val groupWxId = it.key
            val group = it.value
            val now = LocalDateTime.now().plusDays(-1)
            val startTime = now at 0 hour 0 minute 0 second time
            val endTime = now at 23 hour 59 minute 59 second time
            val dailyGroupMessageCount =
                wechatMessagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), groupWxId)
            if (dailyGroupMessageCount.isEmpty()) {
                return@forEach
            }
            val messageSum = wechatMessagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), groupWxId)
            var index = 1
            val stringBuilder = StringBuilder()
            stringBuilder.append("摸鱼排行榜\n")

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
                "本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
            )
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = wechatGroupHasWechatUserMapper.selectByGroupIdAndUserId(groupWxId, it.userWxId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser?.nickName} ，${it.messageCount}条消息\n")
                index++
            }
            wechatBot.getGroupById(groupWxId)?.sendTextMessage(stringBuilder.toString())
        }
    }

    fun sendSyncGroupMember(groupWxId: String) {
        val groupById = wechatBot.getGroupById(groupWxId)
        groupById?.syncGroupMember()
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