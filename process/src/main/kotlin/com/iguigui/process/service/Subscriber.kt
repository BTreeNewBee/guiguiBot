package com.iguigui.process.service

import cn.hutool.core.codec.Base64
import cn.hutool.core.io.FileUtil
import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson.JSONObject
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.github.houbb.opencc4j.util.ZhConverterUtil
import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dao.MessagesMapper
import com.iguigui.process.dao.QqGroupMapper
import com.iguigui.process.dao.QqUserMapper
import com.iguigui.process.dto.neteasecloudmusic.search.SearchResult
import com.iguigui.process.dto.neteasecloudmusic.songsDetail.SongDetail
import com.iguigui.process.entity.GroupHasQqUser
import com.iguigui.process.entity.Messages
import com.iguigui.process.entity.QqGroup
import com.iguigui.process.express.ExpressUtil
import com.iguigui.process.imagegenerator.GeneratorService
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import com.iguigui.process.qqbot.dto.response.appDTO.AppEntity
import com.iguigui.process.util.MessageUtil
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

    @Autowired
    lateinit var generatorService: GeneratorService

    @Autowired
    lateinit var imageService: ImageService

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


    @SubscribeBotMessage(functionName = "你不会百度吗辅助", export = true, description = "输入: 百度+关键词，如百度Jvav")
    fun searchHelper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        val lowercase = contentToString.lowercase(Locale.getDefault())
        searchHelperMap.entries.firstOrNull { lowercase.startsWith(it.key) && lowercase.length > it.key.length }?.let {
            messageAdapter.sendGroupMessage(
                dto.sender.group.id, "这也要我教？？？自己去看\n${
                    it.value + URLEncoder.encode(
                        contentToString.substring(it.key.length).trim(), "UTF-8"
                    )
                }"
            )
        }
    }

    //消息撤回事件，进行撤回重发
    @SubscribeBotMessage(functionName = "撤回重发", export = true, description = "对所有撤回的消息进行重发")
    fun groupRecallMessageEvent(dto: GroupRecallEventDTO) {
        //非自己撤回的不重发
        dto.operator?.id.takeIf { it != dto.authorId }?.let {
            return
        }

        mongoTemplate.find(
            Query.query(
                Criteria.where("messageChain._id").`is`(dto.messageId)
                    .and("sender.group._id").`is`(dto.group.id)
                    .and("messageChain.time")
                    .gt(System.currentTimeMillis() / 1000 - 120)
            ),
            GroupMessagePacketDTO::class.java, "messages"
        ).firstOrNull()?.let { groupMessagePacketDTO ->
            val filter = groupMessagePacketDTO.messageChain.filter { e -> e !is MessageSourceDTO }
            //Check for the presence of advanced messages, cut into multiple lines and send
            filter.count { e ->
                e !is PlainDTO
                        && e !is AtDTO
                        && e !is ImageDTO
                        && e !is FaceDTO
            }
                .takeIf { it > 0 }
                ?.run {
                    messageAdapter.sendGroupMessage(
                        dto.group.id,
                        PlainDTO("${groupMessagePacketDTO.sender.memberName}:"),
                    )
                    messageAdapter.sendGroupMessage(
                        dto.group.id,
                        *filter.toTypedArray()
                    )
                    return@let
                }

            messageAdapter.sendGroupMessage(
                dto.group.id,
                PlainDTO("${groupMessagePacketDTO.sender.memberName}:"),
                *filter.toTypedArray()
            )
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

                val now = LocalDateTime.now()
                val rate = now.dayOfYear / 365.0
                val hue = (120 * (1 - rate)).toInt()
                val color = "hsl($hue, 80%, 45%)"

                val arrayList = ArrayList<RankInfo>()
                dailyGroupMessageCount.forEachIndexed { index, groupMessageCountEntity ->
                    val groupHasQqUser =
                        groupHasQqUserMapper.selectByGroupIdAndQqUserId(it, groupMessageCountEntity.qqUserId)

                    groupHasQqUser.let {
                        arrayList.add(
                            RankInfo(
                                index + 1,
                                Base64.encode(FileUtil.file(imageService.getAvatarImage(groupMessageCountEntity.qqUserId))),
                                it.nickName ?: "",
                                groupMessageCountEntity.messageCount
                            )
                        )
                    }
                }

                val data = MessageRank(
                    now.year,
                    now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    now.dayOfYear,
                    String.format("%.2f", rate * 100.0),
                    color,
                    messageSum,
                    arrayList
                )

                val image =
                    generatorService.generateImage("messageRank.html", data, imageHeight = 270 + 40 * arrayList.size)

                runBlocking {
                    messageAdapter.sendGroupMessage(it, ImageDTO(path = image.absolutePath))
//                    image.delete()
                }
            }
        }
    }


    //进行群成员信息同步
    @SubscribeBotMessage(functionName = "")
    fun memberListEvent(dto: MemberListData) {
        dto.list.forEach { this::syncMember }
    }


    //群信息同步
    @SubscribeBotMessage(functionName = "")
    fun groupListEvent(dto: GroupListData) {
        dto.list.forEach { this::syncGroup }
    }

    //常规图片下载
    @SubscribeBotMessage(functionName = "")
    fun imageDownload(dto: GroupMessagePacketDTO) {
        dto.messageChain.filter { it is ImageDTO }.map { it as ImageDTO }.forEach {
            it.imageId?.let { it1 -> it.url?.let { it2 -> downloadImage(it1, it2) } }
        }
    }

    //
    @SubscribeBotMessage(functionName = "")
    fun groupMessageEventTemplate(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
    }


    //闪照事件监听
    @SubscribeBotMessage(functionName = "闪照重发", export = true, description = "对所有闪照进行重发(有个蛋用现在都不让发了)")
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




    //每次有消息都同步一下
    @SubscribeBotMessage(functionName = "")
    fun groupEvent(dto: GroupMessagePacketDTO) {
        val group = dto.sender.group
        syncGroup(group)
        syncMember(dto.sender)
    }

    //有啥都给存数据库
    @SubscribeBotMessage(functionName = "")
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
    @SubscribeBotMessage(functionName = "点歌台", export = true , description = "网易云点歌，如：点歌 中森明菜")
    fun musicEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (!contentToString.startsWith("点歌")) {
            return
        }
        val senderId = dto.sender.id
        val groupId = dto.sender.group.id
        musicQuestionRecord.remove(senderId)
        contentToString.substring(2).takeIf { it.isNotEmpty() }?.let {
            val json = JSONObject()
            json["keywords"] = it
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
                musicList.add(song.id)
                ms += "${index + 1}. ${song.artists.joinToString(" ") { e -> e.name }} : ${song.name}\n"
            }
            musicQuestionRecord[senderId] = musicList
            messageAdapter.sendGroupMessage(groupId, ms.trimEnd('\n'))
        }
    }


    @SubscribeBotMessage(functionName = "")
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
    @SubscribeBotMessage(functionName = "")
    fun musicShareEvent(dto: GroupMessagePacketDTO) {
        val appDTOMessage = dto.messageChain.filter { e -> e is AppDTO }.map { e -> e as AppDTO }.firstOrNull()
        appDTOMessage?.content?.let {
            val appDTO = Json.decodeFromString(AppEntity.serializer(), it)
            val jumpUrl = appDTO.meta.news.jumpUrl
            val id = Url(jumpUrl).parameters["id"]
        }
    }

//
//    @SubscribeBotMessage(name = "消息统计", export = true)
//    fun currentGroupMessageCount(dto: GroupMessagePacketDTO) {
//        val group = dto.sender.group
//        if (dto.contentToString() == "实时") {
//            val now = LocalDateTime.now()
//            val startTime = now at 0 hour 0 minute 0 second time
//            val endTime = now at 23 hour 59 minute 59 second time
//            val dailyGroupMessageCount =
//                messagesMapper.getDailyGroupMessageCount(startTime.toString(), endTime.toString(), group.id)
//            if (dailyGroupMessageCount.isEmpty()) {
//                return
//            }
//            val messageSum = messagesMapper.getDailyGroupMessageSum(startTime.toString(), endTime.toString(), group.id)
//            val stringBuilder = StringBuilder()
//            stringBuilder.append("龙王排行榜\n")
//            val now1 = LocalDate.now()
//            stringBuilder.append(
//                "今天是${
//                    now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
//                }日，今年的第${now1.dayOfYear}天，您的${now1.year}年使用进度条：\n"
//            )
//            val d = now1.dayOfYear * 1.0 / now1.lengthOfYear() / 2.0
//            var string = "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░"
//            val d1 = (string.length * (0.5 - d)).toInt()
//            stringBuilder.append(
//                "${string.substring(d1, d1 + 20)} ${
//                    String.format(
//                        "%.2f", now1.dayOfYear * 100.0 / now1.lengthOfYear()
//                    )
//                }% \n"
//            )
//            stringBuilder.append(
//                "本群${now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)}日消息总量：${messageSum}条\n"
//            )
//            dailyGroupMessageCount.forEachIndexed { index, groupMessageCountEntity ->
//                stringBuilder.append(
//                    "第${index + 1}名：${
//                        groupHasQqUserMapper.selectByGroupIdAndQqUserId(
//                            group.id,
//                            groupMessageCountEntity.qqUserId!!
//                        ).nickName
//                    } ，${groupMessageCountEntity.messageCount}条消息\n"
//                )
//            }
//            runBlocking {
//                messageAdapter.sendGroupMessage(group.id, stringBuilder.toString())
//            }
//        }
//    }

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