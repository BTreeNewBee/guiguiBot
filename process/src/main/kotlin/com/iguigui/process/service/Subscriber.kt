package com.iguigui.process.service

import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSONObject
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.github.houbb.opencc4j.util.ZhConverterUtil
import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dao.MessagesMapper
import com.iguigui.process.dao.QqGroupMapper
import com.iguigui.process.dao.QqUserMapper
import com.iguigui.process.dto.neteasecloudmusic.search.SearchResult
import com.iguigui.process.dto.neteasecloudmusic.songsDetail.SongDetail
import com.iguigui.process.entity.ExpressSubscriberInfo
import com.iguigui.process.entity.GroupHasQqUser
import com.iguigui.process.entity.Messages
import com.iguigui.process.entity.QqGroup
import com.iguigui.process.express.ExpressStatusEnum
import com.iguigui.process.express.ExpressUtil
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import com.iguigui.process.qqbot.dto.response.appDTO.AppEntity
import com.iguigui.process.util.MessageUtil
import com.iguigui.qqbot.service.impl.WechatMessageServiceImpl
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.koin.ext.isInt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import top.yumbo.util.music.MusicEnum
import top.yumbo.util.music.musicImpl.netease.NeteaseCloudMusicInfo
import java.io.File
import java.net.URLEncoder
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.Resource
import kotlin.collections.ArrayList

@Component
class Subscriber {

    val log = LogFactory.getLog(Subscriber::class.java)!!

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

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var expressUtil: ExpressUtil

    /**
     * Json解析规则，需要注册支持的多态的类
     */
    private val json by lazy {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    lateinit var neteaseCloudMusicInfo: NeteaseCloudMusicInfo


    @PostConstruct
    fun initNeteaseCloudMusic() {
        MusicEnum.setBASE_URL_163Music("http://192.168.50.185:3000")
        neteaseCloudMusicInfo = NeteaseCloudMusicInfo()
    }


    @SubscribeBotMessage(name = "搜索辅助")
    fun searchHelper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        searchHelperMap.entries.forEach {
            if (contentToString.lowercase(Locale.getDefault()).startsWith(it.key)) {
                val substring = contentToString.substring(2)
                if (substring.trim().isNotEmpty()) {
                    runBlocking {
                        messageAdapter.sendGroupMessage(
                            dto.sender.group.id, "这也要我教？？？自己去看\n${
                                it.value + URLEncoder.encode(
                                    substring.trim(), "UTF-8"
                                )
                            }"
                        )
                    }
                }
            }
        }
    }

    //消息撤回事件，进行撤回重发
    @SubscribeBotMessage(name = "撤回重发")
    fun groupRecallMessageEvent(dto: GroupRecallEventDTO) {
        //非自己撤回的不重发
        val id = dto.operator?.id
        if (dto.authorId != id) {
            return
        }
//        val now = LocalDateTime.now()
//        val startTime = now.plusMinutes(-5)
//        var message =
//            messagesMapper.getMessageByMessageId(startTime.toString(), now.toString(), dto.messageId, dto.group.id)
//        message?.let {
//            messageAdapter.sendGroupMessage(dto.group.id, it.senderName + ":" + it.messageDetail)
//        }

        var find = mongoTemplate.find(
            Query.query(
                Criteria.where("messageChain._id").`is`(dto.messageId)
                    .and("sender.group._id").`is`(dto.group.id)
                    .and("messageChain.time")
                    .gt(System.currentTimeMillis() / 1000 - 120)
            ),
            GroupMessagePacketDTO::class.java, "messages"
        )
        find.firstOrNull()?.let {
            val filter = it.messageChain.filter { e -> e !is MessageSourceDTO }
            val count = filter.filter { e -> e !is PlainDTO && e !is AtDTO && e !is ImageDTO && e !is FaceDTO }.count()
            //存在高级消息，拆行发
            if (count > 0) {
                messageAdapter.sendGroupMessage(
                    dto.group.id,
                    PlainDTO("${it.sender.memberName}:"),
                )
                messageAdapter.sendGroupMessage(
                    dto.group.id,
                    *filter.toTypedArray()
                )
            } else {
                messageAdapter.sendGroupMessage(
                    dto.group.id,
                    PlainDTO("${it.sender.memberName}:"),
                    *filter.toTypedArray()
                )
            }

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
                            "%.2f", now1.dayOfYear * 100.0 / now1.lengthOfYear()
                        )
                    }% \n"
                )

                stringBuilder.append(
                    "本群${yesterday.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
                )
                dailyGroupMessageCount.forEach { entity ->
                    val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(it, entity.qqUserId!!)
                    stringBuilder.append("第${index}名：${groupHasQqUser.nickName} ，${entity.messageCount}条消息\n")
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
    @SubscribeBotMessage(name = "", isFunction = false)
    fun memberListEvent(dto: MemberListData) {
        dto.list.forEach { this::syncMember }
    }


    //群信息同步
    @SubscribeBotMessage(name = "", isFunction = false)
    fun groupListEvent(dto: GroupListData) {
        dto.list.forEach { this::syncGroup }
    }

    //常规图片下载
    @SubscribeBotMessage(name = "", isFunction = false)
    fun imageDownload(dto: GroupMessagePacketDTO) {
        dto.messageChain.filter { it is ImageDTO }.map { it as ImageDTO }.forEach {
            it.imageId?.let { it1 -> it.url?.let { it2 -> downloadImage(it1, it2) } }
        }
    }


    //自动快递查询
    @SubscribeBotMessage(name = "快递订阅")
    fun expressEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString.startsWith("订阅快递")) {
            val substring = contentToString.substring(4).lowercase()
            if (substring.isEmpty()) {
                return
            }
            var find = mongoTemplate.find(
                Query.query(
                    Criteria.where("postNumber").`is`(substring)
                ),
                ExpressSubscriberInfo::class.java
            )
            if (find.isNotEmpty()) {
                messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅成功!")
                val first = find.first()
                first.subscriberList.computeIfAbsent(dto.sender.group.id) { ArrayList() }.let {
                    if (!it.contains(dto.sender.id)) {
                        it.add(dto.sender.id)
                    }
                }
                mongoTemplate.save(first)
                sendExpressInfo(first, dto.sender.group.id, dto.sender.id)
                return
            }
            val expressInfo = expressUtil.getExpressInfo(substring)
            if (expressInfo == null) {
                messageAdapter.sendGroupMessage(
                    dto.sender.group.id,
                    "订阅失败,查询不到此快递的信息!"
                )
                return
            }
            val info = expressInfo.data.info
            val statusEnum =
                ExpressStatusEnum.values().filter { e -> e.status == info.currentStatus.toInt() }.firstOrNull()
                    ?: throw RuntimeException("快递订单信息转换失败 ! $expressInfo")
            //0在途，1揽收，2疑难，3签收，4退签，5派件，8清关，14拒签
            when (statusEnum) {
                ExpressStatusEnum.FAILED -> messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅失败,查询不到此快递的信息!")
                ExpressStatusEnum.DELIVERED -> messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅失败,此快递已签收!")
                ExpressStatusEnum.BUYER_REFUSED_TO_SIGN -> messageAdapter.sendGroupMessage(
                    dto.sender.group.id,
                    "订阅失败,此快递已拒签!"
                )
                else -> {
                    messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅成功!")
                    val expressSubscriberInfo = ExpressSubscriberInfo(
                        null,
                        substring,
                        expressInfo.data,
                        mutableMapOf(dto.sender.group.id to Arrays.asList(dto.sender.id))
                    )
                    mongoTemplate.save(
                        expressSubscriberInfo
                    )
                    sendExpressInfo(expressSubscriberInfo, dto.sender.group.id, dto.sender.id)
                }
            }
        }
    }


    @SubscribeBotMessage(name = "取消快递")
    fun expressCancelEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString.startsWith("取消快递")) {
            val substring = contentToString.substring(4).lowercase()
            if (substring.isEmpty()) {
                return
            }
            var find = mongoTemplate.find(
                Query.query(
                    Criteria.where("postNumber").`is`(substring)
                ),
                ExpressSubscriberInfo::class.java
            )
            if (find.isNotEmpty()) {
                messageAdapter.sendGroupMessage(dto.sender.group.id, "取消成功!")
                val first = find.first()
                first.subscriberList[dto.sender.group.id]?.also {
                    it.remove(dto.sender.id)
                }?.let {
                    if (it.size == 0) {
                        first.subscriberList.remove(dto.sender.group.id)
                    }
                }
                mongoTemplate.save(first)
                return
            }
        }
    }


    //
    @SubscribeBotMessage(name = "", isFunction = false)
    fun groupMessageEventTemplate(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
    }


    //
    @SubscribeBotMessage(name = "", isFunction = false)
    fun testGroupMessageEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString == "测试快递") {
            expressScan()
        }
    }


    //扫描快递信息
    fun expressScan() {
        var find = mongoTemplate.find(
            Query.query(
                Criteria.where("exoressData.info.currentStatus").`is`("2")
            ),
            ExpressSubscriberInfo::class.java
        )
        find.forEach {
            val expressInfo = expressUtil.getExpressInfo(it.postNumber)
            if (expressInfo == null) {
                return@forEach
            }
            if (expressInfo.data.info.latestTime != it.exoressData.info.latestTime) {
                it.exoressData = expressInfo.data
                mongoTemplate.save(it)
                sendExpressInfo(it, null, null)
            }
        }
    }

    //发送快递信息
    fun sendExpressInfo(expressInfo: ExpressSubscriberInfo, groupId: Long?, senderId: Long?) {
        val stringBuilder = StringBuilder()
        val data = expressInfo.exoressData
        val now = LocalDateTime.now()
        val pattern1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val pattern2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val parse = LocalDateTime.parse(data.info.sendTime, pattern1)
        val duration: Duration = Duration.between(parse, now)
        stringBuilder.append(" 单号: ${expressInfo.postNumber},${data.company.shortname},当前状态: ${data.info.current},已耗时${duration.toDays()}天${duration.toHoursPart()}小时${duration.toMinutesPart()}分钟\n")
        for (context in data.info.context) {
            stringBuilder.append(
                LocalDateTime.ofEpochSecond(context.time.toLong(), 0, ZoneOffset.ofHours(8)).format(pattern2),
                " ",
                context.desc,
                "\n"
            )
        }
        val substring = stringBuilder.substring(0, stringBuilder.length - 1)
        groupId?.let {
            senderId?.let {
                sendExpressInfo(substring, groupId, senderId)
                return
            }
        }
        expressInfo.subscriberList.entries.forEach {
            sendExpressInfo(
                stringBuilder.toString(),
                it.key,
                *it.value.toLongArray()
            )
        }

    }

    fun sendExpressInfo(expressInfo: String, groupId: Long, vararg senderId: Long) {
        val list = senderId.toTypedArray().map { AtDTO(it) }.map { it as MessageDTO }.toMutableList()
        list.add(PlainDTO(expressInfo))
        messageAdapter.sendGroupMessage(groupId, *list.toTypedArray())
    }


    //闪照事件监听
    @SubscribeBotMessage(name = "闪照重发")
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
        val filePath = "$baseFilePath/${digestHex[digestHex.length - 2]}${digestHex[digestHex.length - 1]}/${imageId}"
        if (!File(filePath).exists()) {
            HttpUtil.downloadFile(url, filePath)
        }
    }


    //改名提醒
    @SubscribeBotMessage(name = "改名提醒")
    fun memberCardChangeEvent(dto: MemberCardChangeEventDTO) {
        messageAdapter.sendGroupMessage(dto.member.group.id, "有人改名字了我不说是谁")
    }

    //每次有消息都同步一下
    @SubscribeBotMessage(name = "", isFunction = false)
    fun groupEvent(dto: GroupMessagePacketDTO) {
        val group = dto.sender.group
        syncGroup(group)
        syncMember(dto.sender)
    }

    //有啥都给存数据库
    @SubscribeBotMessage(name = "", isFunction = false)
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

        mongoTemplate.save(dto, "messages")

//        find = mongoTemplate.find(
//            Query.query(Criteria.where("sender._id").`is`(1342478281)),
//            GroupMessagePacketDTO::class.java, "messages"
//        )
//        println(find)

    }


    val musicQuestionRecord: LinkedHashMap<Long, MutableList<Int>> = linkedMapOf()

    //点歌台
    @SubscribeBotMessage(name = "点歌台")
    fun musicEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (!contentToString.startsWith("点歌")) {
            return
        }
        val senderId = dto.sender.id
        val groupId = dto.sender.group.id
        musicQuestionRecord.remove(senderId)
        val substring = contentToString.substring(2)
        if (substring.isNotEmpty()) {
            val json = JSONObject()
            json["keywords"] = substring
            json["limit"] = 10
            val toJSONString = neteaseCloudMusicInfo.search(json).toJSONString()
            val searchResult = Json.decodeFromString(SearchResult.serializer(), toJSONString)
            val songCount = searchResult.result.songCount
            if (songCount == 0) {
                messageAdapter.sendGroupMessage(groupId, "暂时没有哦")
                return
            }
            var ms = "请输入序号选择：\n"
            val musicList: MutableList<Int> = mutableListOf()
            searchResult.result.songs.forEachIndexed { index, song ->
                run {
                    musicList.add(song.id)
                    ms += "${index + 1}. ${song.artists.map { e -> e.name }.joinToString(" ")} : ${song.name}\n"
                }
            }
            musicQuestionRecord[senderId] = musicList
            messageAdapter.sendGroupMessage(groupId, ms.trimEnd('\n'))
        }
    }


    @SubscribeBotMessage(name = "", isFunction = false)
    fun musicChoseEvent(dto: GroupMessagePacketDTO) {
        val senderId = dto.sender.id
        val groupId = dto.sender.group.id
        val contentToString = dto.contentToString()
        if (contentToString.isInt()) {
            musicQuestionRecord[senderId]?.get(contentToString.toInt() - 1)?.let {
                musicQuestionRecord.remove(senderId)
                val parameter = JSONObject()
                parameter["ids"] = it.toString()
                val toJSONString = neteaseCloudMusicInfo.songDetail(parameter).toJSONString()
                val songsDetail = json.decodeFromString(SongDetail.serializer(), toJSONString)
                val first = songsDetail.songs.first()
                val musicShareDTO = MusicShareDTO(
                    "NeteaseCloudMusic",
                    first.name,
                    first.ar.map { e -> e.name }.joinToString(" "),
                    "https://y.music.163.com/m/song?id=$it&userid=59690957",
                    first.al.picUrl,
                    "http://music.163.com/song/media/outer/url?id=$it&userid=59690957",
                    "[分享]${first.name}",
                )
                messageAdapter.sendGroupMessage(groupId, musicShareDTO)
            }
        }
    }


    //歌单分析器
    @SubscribeBotMessage(name = "", isFunction = false)
    fun musicShareEvent(dto: GroupMessagePacketDTO) {
        val appDTOMessage = dto.messageChain.filter { e -> e is AppDTO }.map { e -> e as AppDTO }.firstOrNull()
        appDTOMessage?.content?.let {
            val appDTO = Json.decodeFromString(AppEntity.serializer(), it)
            val jumpUrl = appDTO.meta.news.jumpUrl
            val id = Url(jumpUrl).parameters["id"]
        }
    }


    @SubscribeBotMessage(name = "消息统计", isFunction = false)
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
                        "%.2f", now1.dayOfYear * 100.0 / now1.lengthOfYear()
                    )
                }% \n"
            )
            stringBuilder.append(
                "本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
            )
            dailyGroupMessageCount.forEach {
                val groupHasQqUser = groupHasQqUserMapper.selectByGroupIdAndQqUserId(group.id, it.qqUserId!!)
                stringBuilder.append("第${index}名：${groupHasQqUser.nickName} ，${it.messageCount}条消息\n")
                index++
            }
            runBlocking {
                messageAdapter.sendGroupMessage(group.id, stringBuilder.toString())
            }
        }
    }

    //一大堆没啥用的搜索辅助
    private val searchHelperMap: Map<String, String> = mapOf(
        "百度" to "https://www.baidu.com/baidu?wd=",
        "谷歌" to "https://www.google.com/search?q=",
        "必应" to "https://cn.bing.com/search?q=",
        "淘宝" to "https://s.taobao.com/search?q=",
        "京东" to "https://search.jd.com/Search?keyword=",
        "微博" to "https://s.weibo.com/weibo?q=",
        "github" to "https://github.com/search?q=",
        "b站" to "https://search.bilibili.com/all?keyword=",
        "不会百度" to "https://buhuibaidu.me/?s=",
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
    return ZhConverterUtil.toSimple(this.messageChain.joinToString("") { it.toString() })
}