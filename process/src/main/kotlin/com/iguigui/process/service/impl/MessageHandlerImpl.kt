package com.iguigui.process.service.impl

import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dao.MessagesMapper
import com.iguigui.process.dao.QqGroupMapper
import com.iguigui.process.dao.QqUserMapper
import com.iguigui.process.entity.GroupHasQqUser
import com.iguigui.process.entity.Messages
import com.iguigui.process.entity.QqGroup
import com.iguigui.process.entity.QqUser
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import com.iguigui.process.service.MessageHandler
import com.iguigui.process.util.MessageUtil
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MusicShare
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
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
class MessageHandlerImpl : MessageHandler {

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

    @Autowired
    lateinit var messageHandler: com.iguigui.process.net.protocol.MessageHandler

    @Value("\${macAddress}")
    lateinit var macAddress: String

    @Value("\${baseFilePath}")
    lateinit var baseFilePath: String


    @Autowired
    lateinit var messageAdapter: MessageAdapter

//    val foodQuestionRecord: LinkedHashMap<Long, MutableList<LocalDateTime>> = linkedMapOf()

//    val musicQuestionRecord: LinkedHashMap<Long, MutableList<MusicShare>> = linkedMapOf()

    override fun handler(message: DTO) {
        println(message)
            when(message) {
                is GroupListData -> syncGrouppList(message)
                is MemberListData -> syncMemberList(message)
            }

    }



    suspend fun processMessageChain(sender: Member, message: MessageChain, id: Int, event: GroupMessageEvent) {
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

        if (contentToString == "实时") {
            currentGroupMessageCount(sender.group)
        }

        //一大堆搜索小助手功能
        searchHelper(contentToString, sender)

        if (contentToString.startsWith("天气")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage(
                    messageUtil.getWeather(URLEncoder.encode(substring, "UTF-8"))
                )
            }
        }

        if (contentToString.startsWith("来点菜")) {
            runBlocking {
                val group = sender.group
                val file = File("C:\\git\\kotlin\\guiguiBot\\src\\main\\resources\\中森明菜_OH_NO,_OH_YES.amr")
                val toExternalResource = file.toExternalResource()
                group.sendMessage(group.uploadAudio(toExternalResource))
                toExternalResource.close()
            }
        }

        if (contentToString.startsWith("minako")) {
            runBlocking {
                val file1 = File("C:\\Users\\atmzx\\Desktop\\morningCallMinako\\minako\\")
                val listFiles = file1.listFiles()
                val group = sender.group
                val file = listFiles.random()
                val toExternalResource = file.toExternalResource()
                group.sendMessage(group.uploadAudio(toExternalResource))
                toExternalResource.close()
            }
        }


        //管那么多图片都给我下回来
        for (singleMessage in message) {
            if (singleMessage is Image) {
                val queryUrl = singleMessage.queryUrl()

                val digestHex = MD5.create().digestHex(singleMessage.imageId)


                val filePath =
                    "$baseFilePath/${digestHex[digestHex.length - 2]}${digestHex[digestHex.length - 1]}/${singleMessage.imageId}"
                if (!File(filePath).exists()) {
                    HttpUtil.downloadFile(queryUrl, filePath)
                }
            }
        }

//
//         Gson版本冲突
//        if (contentToString.startsWith("点歌")) {
//            musicQuestionRecord.remove(sender.id)
//            val musicList: MutableList<MusicShare> = mutableListOf()
//            val songName = contentToString.substring(2).trim()
//            val params: LinkedHashMap<String, Any> = linkedMapOf()
//            var ms = "请输入序号选择：\n"
//            params["s"] = songName
//            params["offset"] = 0
//            params["limit"] = 10
//            params["type"] = 1
//            val post: String = HttpUtil.post("http://music.163.com/api/search/pc", params)
//            val result: com.google.gson.JsonElement = Gson().fromJson(post)
//            try {
//                val totalCount: Int =
//                    result["result"]["songCount"].int //result["result"]["songCount"].toString().toInt()result
//                if (totalCount > 0) {
//                    var count = 1
//                    for (index in 0 until totalCount) {
//                        // 收费的不展示
//                        if (result["result"]["songs"][index]["fee"].int != 1) {
//                            val musicId: String = result["result"]["songs"][index]["id"].toString().trim('\"')
//                            val title: String = result["result"]["songs"][index]["name"].toString().trim('\"')
//                            val artist: String =
//                                result["result"]["songs"][index]["artists"][0]["name"].toString().trim('\"')
//                            val summary: String =
//                                result["result"]["songs"][index]["album"]["name"].toString().trim('\"')
//                            val pictureUrl: String =
//                                result["result"]["songs"][index]["album"]["blurPicUrl"].toString().trim('\"')
//                            val jumpUrl = "https://y.music.163.com/m/song/$musicId"
//                            val musicUrl = "http://music.163.com/song/media/outer/url?id=$musicId.mp3"
//                            val brief = "[分享]$title"
//                            musicList.add(
//                                MusicShare(
//                                    MusicKind.NeteaseCloudMusic,
//                                    title,
//                                    summary,
//                                    jumpUrl,
//                                    pictureUrl,
//                                    musicUrl,
//                                    brief
//                                )
//                            )
//                            ms += "$count. $artist  $title"
//                            count++
//                            if (count > 5) {
//                                break
//                            }
//                            ms += "\n"
//                        }
//                    }
//                    musicQuestionRecord[sender.id] = musicList
//                    runBlocking {
//                        sender.group.sendMessage(ms)
//                    }
//                } else {
//                    runBlocking {
//                        sender.group.sendMessage("搜锤子呢，没有这鸽")
//                    }
//                }
//
//            } catch (e: Exception) {
//                runBlocking {
//                    sender.group.sendMessage("不要搜一些乱七八糟的东西行不行")
//                }
//            }
//        }

//        try {
//            val num: Int = contentToString.toInt() - 1
//            val musicList: MutableList<MusicShare>? = musicQuestionRecord[sender.id]
//            if (musicList != null) {
//                if (num >= 0 && num < musicList.size) {
//                    runBlocking {
//                        val ms: MusicShare = musicList[num]
//                        sender.group.sendMessage(ms)
//                    }
//                    musicQuestionRecord.remove(sender.id)
//                } else {
//                    runBlocking {
//                        sender.group.sendMessage("选锤子呢")
//                    }
//                }
//            }
//        } catch (e: NumberFormatException) {
//        }
//
//        val get1 = message.get(1)
//        println(get1)
//        if (get1 is At) {
//            val get2 = message.get(2)
//            println(get2)
//            if (get2 != null) {
//                val msgContent = get2.contentToString()
//                var words: String
//                val get = HttpUtil.get(
//                    "http://api.qingyunke.com/api.php?key=free&appid=0&msg=${
//                        URLEncoder.encode(
//                            msgContent.trim(),
//                            "UTF-8"
//                        )
//                    }"
//                )
//                println(get)
//                words = Json.parseToJsonElement(get).jsonObject["content"].toString().replace("{br}","\n").replace("\"","")
//                println(words)
//                words.let {
//                    sender.group.sendMessage(words)
//                }
//            }
//        }

    }


    fun currentGroupMessageCount(group: Group) {
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
            group.sendMessage(stringBuilder.toString())
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


    private val searchHelperMap : Map<String,String> = mapOf(
        "百度" to "https://www.baidu.com/baidu?wd=",
        "谷歌" to "https://www.google.com/search?q=",
        "必应" to "https://cn.bing.com/search?q=",
        "淘宝" to "https://s.taobao.com/search?q=",
        "github" to "https://github.com/search?q=",
        "b站" to "https://search.bilibili.com/all?keyword=",
    )



    private fun searchHelper(contentToString: String, sender: Member) {
        searchHelperMap.entries.forEach {
            if (contentToString.lowercase(Locale.getDefault()).startsWith(it.key)) {
                val substring = contentToString.substring(2)
                if (substring.trim().isNotEmpty()) {
                    runBlocking {
                        sender.group.sendMessage(
                            "这也要我教？？？自己去看\n${it.value + URLEncoder.encode(
                                substring.trim(),
                                "UTF-8"
                            )}"
                        )
                    }
                }
            }
        }
    }


    private fun syncMemberList(message: MemberListData) {
        message.list.forEach {
            syncMember(it)
        }
    }


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

    private fun syncGrouppList(groupListData: GroupListData) {
        groupListData.list.forEach {
            syncGroup(it)
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

}