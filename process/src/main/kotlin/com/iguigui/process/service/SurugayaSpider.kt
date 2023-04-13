package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.controller.WebHook17Tracks
import com.iguigui.process.dao.GroupHasQqUserMapper
import com.iguigui.process.dto.mercari.MercariResponse
import com.iguigui.process.entity.mongo.MercariSpiderContent
import com.iguigui.process.entity.mongo.MercariSubscribe
import com.iguigui.process.entity.mongo.MercariSubscribeInfo
import com.iguigui.process.imagegenerator.GeneratorService
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import com.ruiyun.jvppeteer.core.browser.Browser
import com.ruiyun.jvppeteer.core.page.Page
import com.ruiyun.jvppeteer.core.page.Request
import com.ruiyun.jvppeteer.core.page.Response
import com.ruiyun.jvppeteer.options.Viewport
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import org.koin.ext.isInt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.Resource
import kotlin.collections.ArrayList
import kotlin.time.measureTime

//煤炉爬虫
@Component
class SurugayaSpider {

    val log = org.slf4j.LoggerFactory.getLogger(SurugayaSpider::class.java)

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var generatorService: GeneratorService

    @Autowired
    lateinit var groupHasQqUserMapper: GroupHasQqUserMapper

    @Autowired
    lateinit var browser: Browser

    private var json = Json {
        ignoreUnknownKeys = true

    }

    //改名提醒
    @SubscribeBotMessage(
        functionName = "骏河屋订阅", export = true, description = "" +
                "设定关键词，自动监测骏河屋是否有新东西上架\n" +
                "添加订阅：格式为'骏河屋订阅 关键词 是否全新 价格区间\n" +
                "如：骏河屋订阅 山口百惠 非全新 200-500\n" +
                "如：骏河屋订阅 中森明菜 全新 1000-5000\n" +
                "查看已订阅的列表：骏河屋订阅"
    )
    suspend fun surugayaSpider(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString.startsWith("骏河屋订阅")) {
            val split = contentToString.split(" ")
            if (split.size == 1) {
                val find = mongoTemplate.find(
                    Query.query(
                        Criteria.where("groupId").`is`(dto.sender.group.id)
                    ),
                    MercariSubscribe::class.java, "mercariSubscribe"
                )
                if (find.isEmpty()) {
                    sendSubscribeMessage(
                        MercariSubscribe(
                            id = null,
                            groupId = dto.sender.group.id,
                            subscribe = arrayListOf()
                        )
                    )
                } else {
                    sendSubscribeMessage(find.first())
                }
            } else if (split.size == 4) {
                val keyword = split[1]
                val condition = split[2]
                val priceRange = split[3]
                val priceRangeSplit = priceRange.split("-")
                if (priceRangeSplit.size != 2 || !priceRangeSplit[0].isInt() || !priceRangeSplit[1].isInt()) {
                    messageAdapter.sendGroupMessage(dto.sender.group.id, "价格区间格式错误")
                    return
                }

                val mercariSubscribeInfo = MercariSubscribeInfo(
                    keyword = keyword,
                    condition = condition,
                    priceRange = priceRange,
                    userId = dto.sender.id
                )
                val find = mongoTemplate.find(
                    Query.query(
                        Criteria.where("groupId").`is`(dto.sender.group.id)
                    ),
                    MercariSubscribe::class.java, "mercariSubscribe"
                )
                if (find.isEmpty()) {
                    val mercariSubscribe = MercariSubscribe(
                        id = null,
                        groupId = dto.sender.group.id,
                        subscribe = arrayListOf(
                            mercariSubscribeInfo
                        )
                    )
                    mongoTemplate.save(mercariSubscribe, "mercariSubscribe")
                    messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅成功")
                    getLastestGoodInfo(dto.sender.group.id, mercariSubscribeInfo)
                } else {
                    val subscribe = find.first().subscribe
                    if (subscribe.size > 2) {
                        messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅数量超过上限")
                        return
                    }

                    if (subscribe.contains(mercariSubscribeInfo)) {
                        messageAdapter.sendGroupMessage(dto.sender.group.id, "已经订阅过了")
                    } else {
                        subscribe.add(
                            mercariSubscribeInfo
                        )
                        mongoTemplate.save(find.first(), "mercariSubscribe")
                        messageAdapter.sendGroupMessage(dto.sender.group.id, "订阅成功")
                        getLastestGoodInfo(dto.sender.group.id, mercariSubscribeInfo)
                    }
                }
            } else {
                messageAdapter.sendGroupMessage(dto.sender.group.id, "格式错误")
            }
        }
    }

    @SubscribeBotMessage("取消煤炉", "取消煤炉订阅", false)
    fun unSubscriber(message: GroupMessagePacketDTO) {
        val contentToString = message.contentToString()
        if (contentToString.startsWith("取消煤炉")) {
            val find = mongoTemplate.find(
                Query.query(
                    Criteria.where("groupId").`is`(message.sender.group.id)
                ),
                MercariSubscribe::class.java, "mercariSubscribe"
            )
            if (contentToString.length == 4) {
                find.firstOrNull()?.let {
                    sendSubscribeMessage(it)
                }
            } else {
                val split = contentToString.substring(4)
                if (!split.isInt()) {
                    return
                }
                val i = split.toInt() - 1
                find.firstOrNull()?.subscribe?.let {
                    if (it.size <= i) {
                        messageAdapter.sendGroupMessage(message.sender.group.id, "取消订阅失败")
                        return
                    }
                    if (it[i].userId != message.sender.id) {
                        messageAdapter.sendGroupMessage(message.sender.group.id, "取消订阅失败，只能取消自己订阅的")
                        return
                    }
                    val removeAt = it.removeAt(split.toInt() - 1)
                    mongoTemplate.save(find.first(), "mercariSubscribe")
                    messageAdapter.sendGroupMessage(
                        message.sender.group.id,
                        "取消订阅 ${removeAt.keyword} ${removeAt.condition} ${removeAt.priceRange} 成功"
                    )
                }
            }
        }
    }

    //发送订阅信息
    private fun sendSubscribeMessage(mercariSubscribe: MercariSubscribe) {
        val subscribe = mercariSubscribe.subscribe
        val arrayList = ArrayList<Triple<Int, String?, String>>()
        subscribe.forEachIndexed { index, info ->
            val groupHasQqUser =
                groupHasQqUserMapper.selectByGroupIdAndQqUserId(mercariSubscribe.groupId, info.userId)
            groupHasQqUser.let {
                arrayList.add(
                    Triple(
                        index + 1,
                        groupHasQqUser.nickName,
                        "${info.keyword} ${info.condition} ${info.priceRange}"
                    )
                )
            }
        }
        val image = generatorService.generateImage(
            "mercariSubscribeList.html", arrayList,
            imageHeight = 240 + 40 * mercariSubscribe.subscribe.size
        )
        messageAdapter.sendGroupMessage(mercariSubscribe.groupId, ImageDTO(path = image.absolutePath))
    }

    //每一个小时检查一次
    @Scheduled(cron = "0 0 * * * ?")
    fun mercariSpider() {
        val find = mongoTemplate.findAll(MercariSubscribe::class.java, "mercariSubscribe")
        find.forEach {
            it.subscribe.forEach { info ->
                CoroutineScope(Dispatchers.Default).launch {
                    getLastestGoodInfo(it.groupId, info)
                }
            }
        }
    }

    private suspend fun getLastestGoodInfo(groupId: Long, info: MercariSubscribeInfo) {
        val split = info.priceRange.split("-")
        var url =
            "https://jp.mercari.com/search?keyword=${info.keyword}&sort=created_time&order=desc&status=on_sale&price_min=${split[0]}&price_max=${split[1]}"
        if (info.condition == "全新") {
            url += "&item_condition_id=1"
        }
        log.info("subscribe url:{}", url)
        getLastestGoodInfo(url, groupId, info)
    }


    private suspend fun getLastestGoodInfo(url: String, groupId: Long, info: MercariSubscribeInfo) {
        val page: Page = browser.newPage()
        pageLoadOptimize(page)
        log.info("start url: {}", url)
        page.onResponse { response: Response ->
            val xhrUrl = response.url()
            log.info("response url: {}", xhrUrl)
            val text = response.text()
            if (xhrUrl == "https://api.mercari.jp/v2/entities:search" && text.isNotEmpty()) {
                try {
                    val mercariResponse = json.decodeFromString(MercariResponse.serializer(), text)
                    val items = mercariResponse.items
                    val find = mongoTemplate.find(
                        Query.query(
                            Criteria.where("key").`is`("${info.keyword}${info.condition}${info.priceRange}")
                        ),
                        MercariSpiderContent::class.java, "mercariSpiderContent"
                    )
                    if (find.isEmpty()) {
                        MercariSpiderContent(
                            id = null,
                            key = "${info.keyword}${info.condition}${info.priceRange}",
                            items = items
                        ).let {
                            mongoTemplate.save(it, "mercariSpiderContent")
                        }
                        page.close()
                        log.info("page close url: {}", url)
                        return@onResponse
                    }
                    find.first().let { content ->
                        val itemMap = content.items.associateBy { it.id }
                        val newList = items.subList(0, 50).filter { !itemMap.containsKey(it.id) }.toList()
                        content.items = items
                        mongoTemplate.save(content, "mercariSpiderContent")
                        if (newList.isNotEmpty()) {
                            val list = mutableListOf(AtDTO(info.userId), PlainDTO("新发现上架${newList.size}个商品"))
                            messageAdapter.sendGroupMessage(groupId, *list.toTypedArray())
                            //时间戳转localdatetime


                            newList.forEach {
                                messageAdapter.sendGroupMessage(
                                    groupId,
                                    "名称：${it.name}\n" +
                                            "状态：${conditionParse(it.itemConditionId)}\n" +
                                            "价格：￥${it.price}\n" +
                                            "上架时间：${it.created.asyyyyMMdd()}" +
                                            "更新时间：${it.updated.asyyyyMMdd()}" +
                                            "链接：https://jp.mercari.com/item/${it.id}\n"
                                )
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                page.close()
                log.info("page close url: {}", url)
            }
        }
        withContext(Dispatchers.IO) {
            page.goTo(url)
        }
        page.setViewport(Viewport(1920, 1080, 1.0, false, false, false))
    }

    private fun conditionParse(status: String): String {
        return when (status) {
            "1" -> "新品、未使用"
            "2" -> "未使用に近い"
            "3" -> "目立った傷や汚れなし"
            "4" -> "やや傷や汚れあり"
            "5" -> "傷や汚れあり"
            "6" -> "全体的に状態が悪い"
            else -> "未知"
        }
    }


    //page加载优化
    private fun pageLoadOptimize(page: Page) {
        page.setDefaultNavigationTimeout(60 * 1000)
        page.setCacheEnabled(true)
        page.setRequestInterception(true)
        page.onRequest { request: Request ->
            try {
                val url = request.url()
                when (request.resourceType()) {
                    "image" -> {
                        request.abort()
                    }

                    "media" -> {
                        request.abort()
                    }

                    "font" -> {
                        request.abort()
                    }

                    "texttrack" -> {
                        request.abort()
                    }

                    "object" -> {
                        request.abort()
                    }

                    "beacon" -> {
                        request.abort()
                    }

                    "csp_report" -> {
                        request.abort()
                    }

                    "imageset" -> {
                        request.abort()
                    }

                    else -> {
                        log.info("request url:{}", url)
                        request.continueRequest()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun String.asyyyyMMdd() = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this.toLong() * 1000),
        ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


}