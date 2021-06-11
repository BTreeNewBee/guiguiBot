package com.iguigui.qqbot.service.impl

import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import com.baidu.aip.ocr.AipOcr
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.int
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.iguigui.qqbot.dao.GroupHasQqUserMapper
import com.iguigui.qqbot.dao.MessagesMapper
import com.iguigui.qqbot.dao.QqGroupMapper
import com.iguigui.qqbot.dao.QqUserMapper
import com.iguigui.qqbot.entity.GroupHasQqUser
import com.iguigui.qqbot.entity.Messages
import com.iguigui.qqbot.entity.QqGroup
import com.iguigui.qqbot.entity.QqUser
import com.iguigui.qqbot.service.MessageService
import com.iguigui.qqbot.util.MessageUtil
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.annotation.Resource
import kotlin.math.log
import kotlin.random.Random


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

    @Resource
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var bot: Bot

//    @Autowired
//    lateinit var aipOcr: AipOcr

    @Value("\${macAddress}")
    lateinit var macAddress: String

    @Value("\${baseFilePath}")
    lateinit var baseFilePath: String

    var foods: List<String> = listOf("馄饨", "拉面", "烩面", "热干面", "刀削面", "油泼面", "炸酱面", "炒面", "重庆小面", "米线", "酸辣粉", "土豆粉", "螺狮粉", "凉皮儿", "麻辣烫", "肉夹馍", "羊肉汤", "炒饭", "盖浇饭", "卤肉饭", "烤肉饭", "黄焖鸡米饭", "驴肉火烧", "川菜", "麻辣香锅", "火锅", "酸菜鱼", "烤串", "披萨", "烤鸭", "汉堡", "炸鸡", "寿司", "蟹黄包", "煎饼果子", "生煎", "炒年糕")

    val foodQuestionRecord: LinkedHashMap<Long, MutableList<LocalDateTime>> = linkedMapOf()

    val musicQuestionRecord: LinkedHashMap<Long, MutableList<MusicShare>> = linkedMapOf()

    @Transactional
    override fun processMessage(event: GroupMessageEvent) {
        println("message process")
        runBlocking {
            syncGroup(event.group)
            syncUser(event.sender)
            syncMember(event.group, event.sender)
            processMessageChain(event.sender, event.message, event.source.ids.first(), event)
        }

    }

    override fun processCancelMessage(event: MessageRecallEvent.GroupRecall) {
        println("cancelMessage process")
        runBlocking {
            event.messageIds.forEach {
                sendCancelMessage(it, event.group)
            }
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

        if (contentToString == "/查询实时记录") {
            currentGroupMessageCount(sender.group);
        }

        if (contentToString.startsWith("百度")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage(
                        "怎么百度也要我教？？？自己去看：https://www.baidu.com/baidu?wd=" + URLEncoder.encode(
                                substring,
                                "UTF-8"
                        )
                )
            }
        }

        if (contentToString.startsWith("谷歌")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage(
                        "谷歌也要我教？？？自己去看：https://www.google.com/search?q=" + URLEncoder.encode(
                                substring,
                                "UTF-8"
                        )
                )
            }
        }

        if (contentToString.startsWith("必应")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage(
                        "必应也要我教？？？自己去看：https://cn.bing.com/search?q=" + URLEncoder.encode(
                                substring,
                                "UTF-8"
                        )
                )
            }
        }

        if (contentToString.startsWith("天气")) {
            val substring = contentToString.substring(2)
            runBlocking {
                sender.group.sendMessage(
                        messageUtil.getWeather(URLEncoder.encode(substring, "UTF-8"))
                )
            }
        }

        if ((contentToString.contains("早上") || contentToString.contains("中午") || contentToString.contains("晚上"))
                && (contentToString.contains("吃点啥") || contentToString.contains("吃什么") || contentToString.contains("吃啥"))) {
            var res = ""
            val timeList: MutableList<LocalDateTime>? = foodQuestionRecord[sender.id]
            if (timeList != null && timeList.size >= 3) {
                if (timeList[timeList.size - 1].minusSeconds(30) <= timeList[timeList.size - 3]) {
                    res = "爱吃吃，不吃拉倒，爷不伺候了"
                    foodQuestionRecord.remove(sender.id)
                }
            } else if(timeList == null) {
                res = "哟，爷，来了！吃点" + foods[(foods.indices).random()] + "怎么样？"
                foodQuestionRecord[sender.id] = mutableListOf(LocalDateTime.now())
            } else {
                res = foods[(foods.indices).random()] + "？"
                foodQuestionRecord[sender.id]?.add(LocalDateTime.now())
            }
            runBlocking {
                sender.group.sendMessage(res)
            }
        }

        //管那么多图片都给我下回来
        for (singleMessage in message) {
            if (singleMessage is Image) {
                val queryUrl = singleMessage.queryUrl()

                val digestHex = MD5.create().digestHex(singleMessage.imageId)


                val filePath = "$baseFilePath/${digestHex[digestHex.length - 2]}${digestHex[digestHex.length - 1]}/${singleMessage.imageId}"
                if (!File(filePath).exists()) {
                    HttpUtil.downloadFile(queryUrl, filePath)
                }
            }
        }

//        //查询虚拟币行情
//        if (contentToString) {
//
//        }

        if (contentToString.startsWith("点歌")) {
            musicQuestionRecord.remove(sender.id)
            val musicList: MutableList<MusicShare> = mutableListOf()
            val songName = contentToString.substring(2).trim()
            val params: LinkedHashMap<String, Any> = linkedMapOf()
            var ms = ""
            params["s"] = songName
            params["offset"] = 0
            params["limit"] = 10
            params["type"] = 1
            val post:String = HttpUtil.post("http://music.163.com/api/search/pc", params)
            val result: JsonElement = Gson().fromJson(post)
            println(result)
            println(result["result"])
            try {
                val totalCount: Int = result["result"]["songCount"].int //result["result"]["songCount"].toString().toInt()result
                if (totalCount > 0) {
                    var count = 1
                    for (index in 0 until totalCount) {
                        // 收费的不展示
                        if (result["result"]["songs"][index]["fee"].int != 1) {
                            val musicId: String = result["result"]["songs"][index]["id"].toString().trim('\"')
                            val title: String = result["result"]["songs"][index]["name"].toString().trim('\"')
                            val artist: String = result["result"]["songs"][index]["artists"][0]["name"].toString().trim('\"')
                            val summary: String = result["result"]["songs"][index]["album"]["name"].toString().trim('\"')
                            val pictureUrl: String = result["result"]["songs"][index]["album"]["blurPicUrl"].toString().trim('\"')
                            val jumpUrl = "https://y.music.163.com/m/song/$musicId"
                            val musicUrl = "http://music.163.com/song/media/outer/url?id=$musicId.mp3"
                            val brief = "[分享]$title"
                            musicList.add(MusicShare(MusicKind.NeteaseCloudMusic, title, summary, jumpUrl, pictureUrl, musicUrl, brief))
                            println(title)
                            println(artist)
                            ms += "$count. $artist  $title"
                            count++
                            if (count > 5) {
                                break
                            }
                            ms += "\n"
                        }
                    }
                    musicQuestionRecord[sender.id] = musicList
                    runBlocking {
                        sender.group.sendMessage(ms)
                    }
                } else {
                    runBlocking {
                        sender.group.sendMessage("搜锤子呢，没有这鸽")
                    }
                }

            } catch (e : Exception) {
                runBlocking {
                    sender.group.sendMessage("不要搜一些乱七八糟的东西行不行")
                }
            }
        }

        try {
            val num:Int = contentToString.toInt() - 1
            val musicList: MutableList<MusicShare>? = musicQuestionRecord[sender.id]
            if (musicList != null) {
                if (num >= 0 && num < musicList.size) {
                    runBlocking {
                        val ms:MusicShare = musicList[num]
                        sender.group.sendMessage(ms)
                    }
                    musicQuestionRecord.remove(sender.id)
                } else {
                    runBlocking {
                        sender.group.sendMessage("选锤子呢")
                    }
                }
            }
        } catch (e: NumberFormatException) {
            println("不是数字")
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
            val dailyGroupMessageCount =
                    messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
            if (dailyGroupMessageCount.isEmpty()) {
                continue
            }
            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
            var index = 1
            val stringBuilder = StringBuilder()
            stringBuilder.append("龙王排行榜\n")

            val now1 = LocalDate.now()
            stringBuilder.append("今天是${now1.format(DateTimeFormatter.ISO_LOCAL_DATE)}日，今年的第${now1.dayOfYear}天，您的${now1.year}年使用进度条：\n")
            val d = now1.dayOfYear * 1.0 / now1.lengthOfYear() / 2.0
            var string = "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░"
            val d1 = (string.length * (0.5 - d)).toInt()
            stringBuilder.append("${string.substring(d1, d1 + 20)} ${
                String.format(
                        "%.2f",
                        now1.dayOfYear * 100.0 / now1.lengthOfYear()
                )
            }% \n")

            stringBuilder.append(
                    "本群${yesterday.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
            )
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(group.id, it.qqUserId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser.nameCard} ，${it.messageCount}条消息\n")
                index++
            }
            stringBuilder.append("晚安~")
            runBlocking {
                group.sendMessage(stringBuilder.toString())
            }
        }
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
        stringBuilder.append("今天是${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日，今年的第${now1.dayOfYear}天，您的${now1.year}年使用进度条：\n")
        val d = now1.dayOfYear * 1.0 / now1.lengthOfYear() / 2.0
        var string = "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░"
        val d1 = (string.length * (0.5 - d)).toInt()
        stringBuilder.append("${string.substring(d1, d1 + 20)} ${
            String.format(
                    "%.2f",
                    now1.dayOfYear * 100.0 / now1.lengthOfYear()
            )
        }% \n")
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


    override fun processFriendMessage(friendMessageEvent: FriendMessageEvent) {
        if (friendMessageEvent.sender.id == 1479712749L
                && friendMessageEvent.message.contentToString() == "开机") {
            startUpMyComputer()
        }
        if(friendMessageEvent.sender.id == 545784329L) {
//            var res = ""
//            val timeList: MutableList<LocalDateTime>? = foodQuestionRecord[545784329L]
//            if (timeList != null && timeList.size >= 3) {
//                if (timeList[timeList.size - 1].minusSeconds(30) <= timeList[timeList.size - 3]) {
//                    res = "爱吃吃，不吃拉倒，爷不伺候了"
//                    foodQuestionRecord.remove(545784329L)
//                }
//            } else if(timeList == null) {
//                res = "哟，爷，来了！吃点" + foods[(foods.indices).random()] + "怎么样？"
//                foodQuestionRecord[545784329L] = mutableListOf(LocalDateTime.now())
//            } else {
//                res = foods[(foods.indices).random()] + "？"
//                foodQuestionRecord[545784329L]?.add(LocalDateTime.now())
//            }
//            val friend = bot.getFriend(545784329)
//            if (friend != null) {
//                runBlocking {
//                    friend.sendMessage(res)
//                }
//            }
//            val friend = bot.getFriend(545784329)
//            if (friend != null) {
//                testMusic(friend)
//            }
        }
    }


    override fun listeningCryptocurrencySchedule() {
//        getBinance()
//        getHuobi()
    }

    override fun sendWeather(friend: Friend, city : String) {
        runBlocking {
            friend.sendMessage(messageUtil.getWeather(city))
        }
    }


//    var binanceArticle = HashSet<String>()
//
//    fun getBinance() {
//        var url = "https://www.binance.com/bapi/composite/v1/public/cms/article/catalog/list/query?catalogId=48&pageNo=1&pageSize=15"
//        val get = HttpUtil.createGet(url).header("lang", "zh-CN").execute().body()
//        var json: JsonElement = Gson().fromJson(get)
//        val articleArray = json.asJsonObject.getAsJsonObject("data").asJsonArray
//        if (binanceArticle.size < 15) {
//            for (jsonElement in articleArray) {
//                val asJsonObject = jsonElement.asJsonObject
//                val code = asJsonObject.getAsJsonObject("code").asString
//                binanceArticle.add(code)
//                println(jsonElement)
//            }
//            return
//        }
//        var binanceArticleNew = HashSet<String>()
//        for (jsonElement in articleArray) {
//            val asJsonObject = jsonElement.asJsonObject
//            val title = asJsonObject.getAsJsonObject("title").asString
//            val code = asJsonObject.getAsJsonObject("code").asString
//            binanceArticleNew.add(code)
//            if (!binanceArticle.contains(code)) {
//                println("发现新文章")
//                if (title.contains("上市")) {
//                    println("发现新上市文章")
//                    runBlocking {
//                        bot.getGroup(694967597)!!.sendMessage("发现新币上市公告，公告标题：$title ，公告地址：https://www.binance.com/zh-CN/support/announcement/$code")
//                    }
//                }
//            }
//        }
//        binanceArticle = binanceArticleNew
//    }
//
//    var huobiArticle = HashSet<String>()
//
//    fun getHuobi() {
//        var url = "https://www.huobi.pe/support/public/getList"
//        val hashMap = HashMap<String, Any>()
//        hashMap["language"] = "zh-cn"
//        hashMap["page"] = 1
//        hashMap["limit"] = 20
//        hashMap["oneLevelId"] = 360000031902
//        hashMap["twoLevelId"] = 360000039942
//        val get = HttpUtil.createPost(url).form(hashMap).header("Accept-Language", "zh-cn").execute().body()
//        var json: JsonElement = Gson().fromJson(get)
//        val articleArray = json.asJsonObject.getAsJsonObject("data").asJsonObject.getAsJsonArray("list")
//        if (huobiArticle.size < 20) {
//            for (jsonElement in articleArray) {
//                val asJsonObject = jsonElement.asJsonObject
//                val id = asJsonObject.getAsJsonObject("id").asString
//                huobiArticle.add(id)
//                println(jsonElement)
//            }
//            return
//        }
//        var huobiArticleNew = HashSet<String>()
//        for (jsonElement in articleArray) {
//            val asJsonObject = jsonElement.asJsonObject
//            val title = asJsonObject.getAsJsonObject("title").asString
//            val id = asJsonObject.getAsJsonObject("id").asString
//            huobiArticleNew.add(id)
//            if (!huobiArticle.contains(id)) {
//                println("发现新文章")
//                runBlocking {
//                    bot.getGroup(694967597)!!.sendMessage("发现新币上市公告，公告标题：$title ，https://www.huobi.pe/support/zh-cn/detail/$id")
//                }
//            }
//        }
//        huobiArticle = huobiArticleNew
//    }


    //把我的电脑开机
    fun startUpMyComputer() {

        val command = ByteArray(102)
        for (i in 0..6) {
            command[i] = 0xff.toByte()
        }
        val split = macAddress.split("-")
        val macArray = ByteArray(6)
        for ((index, value) in split.withIndex()) {
            macArray[index] = value.toInt(16).toByte()
        }
        for (i in 0..15) {
            System.arraycopy(macArray, 0, command, (i + 1) * 6, 6)
        }

        println(bytesToHexString(command))

        val address = InetAddress.getByName("255.255.255.255")
        val port = 7
        val packet = DatagramPacket(command, command.size, address, port)
        val socket = DatagramSocket()
        socket.send(packet)

    }

    fun bytesToHexString(src: ByteArray?): String? {
        val stringBuilder = StringBuilder("")
        if (src == null || src.isEmpty()) {
            return null
        }
        for (element in src) {
            val v = element.toInt() and 0xFF
            val hv = Integer.toHexString(v)
            stringBuilder.append("0x")
            if (hv.length < 2) {
                stringBuilder.append("0,")
            }
            stringBuilder.append("$hv,")
        }
        return stringBuilder.toString()
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

    private suspend fun syncUser(member: Member) {
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
    }

    private suspend fun syncMember(group: Group, member: Member) {
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