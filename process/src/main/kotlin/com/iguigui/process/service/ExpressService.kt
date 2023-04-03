package com.iguigui.process.service

import cn.hutool.http.HttpUtil
import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.process.controller.WebHook17Tracks
import com.iguigui.process.dto.tracks17.WebHookTracks17
import com.iguigui.process.dto.tracks17.dto.RegisterRequest
import com.iguigui.process.dto.tracks17.dto.RegisterResponse
import com.iguigui.process.entity.Carrier
import com.iguigui.process.entity.CarrierInfo
import com.iguigui.process.entity.ExpressSubscriberInfo
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.AtDTO
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.MessageDTO
import com.iguigui.process.qqbot.dto.PlainDTO
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class ExpressService {

    val log = org.slf4j.LoggerFactory.getLogger(ExpressService::class.java)

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Value("\${track17Key}")
    lateinit var track17Key: String

    private var carrierInfo: Map<Int, Carrier> = HashMap()

    private var carrierInfoStrMap: Map<String, Carrier> = HashMap()

    @PostConstruct
    fun init() {
        var find1 = mongoTemplate.find(
            Query.query(
                Criteria()
            ),
            CarrierInfo::class.java
        )
        carrierInfo = find1.first().run {
            data.associateBy { it.key }
        }
        carrierInfoStrMap = find1.first().run {
            data.associateBy { it.nameZhCn.lowercase() }
        }
    }

    //自动快递查询
    @SubscribeBotMessage(name = "快递订阅", export = true, description = """
                    使用方法：
                    1. 简易模式自动识别快递公司, 例如: 订阅快递 123456789
                    2. 如果出现"快递公司不能被识别。",请指定快递公司订阅,例如: 订阅快递 中通快递 123456789
                    3. 取消订阅快递, 例如: 取消快递 123456789
                """)
    fun expressEvent(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString.startsWith("订阅快递")) {
            var postNumber = contentToString.substring(4).lowercase().trim()
            if (postNumber.isEmpty()) {
                return
            }
            val split = postNumber.split(" ")
            var carrierKey: Int? = null
            if (split.size > 1) {
                var carrier = split.subList(0, split.size - 1).joinToString(" ").trim()
                postNumber = split[split.size - 1]
                if (carrierInfoStrMap[carrier.lowercase()] == null) {
                    messageAdapter.sendGroupMessage(dto.sender.group.id, "没有找到该快递公司")
                    return
                }
                carrierKey = carrierInfoStrMap[carrier.lowercase()]?.key
            }

            var find = mongoTemplate.find(
                Query.query(
                    Criteria.where("postNumber").`is`(postNumber)
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
            val expressSubscriberInfo = ExpressSubscriberInfo(
                _id = null,
                postNumber = postNumber,
                carrier = null,
                trackData = null,
                subscriberList = HashMap()
            )
            expressSubscriberInfo.subscriberList.computeIfAbsent(dto.sender.group.id) { ArrayList() }.let {
                if (!it.contains(dto.sender.id)) {
                    it.add(dto.sender.id)
                }
            }
            val asList = listOf(RegisterRequest(postNumber, carrierKey))
            HttpUtil.createPost("https://api.17track.net/track/v2/register").header("Content-Type", "application/json")
                .header("17token", track17Key).body(
                    Json.encodeToString(asList)
                ).execute().body().let {
                    if (it.isNotEmpty()) {
                        log.info(it)
                        val registerResponse = Json.decodeFromString<RegisterResponse>(it)
                        registerResponse.data.rejected.firstOrNull { it.number.lowercase() == postNumber.lowercase() }
                            ?.run {
                                messageAdapter.sendGroupMessage(
                                    dto.sender.group.id,
                                    "订阅失败!${errorMessageParse(error.code)}"
                                )
                                return
                            }
                        registerResponse.data.accepted.firstOrNull { it.number.lowercase() == postNumber.lowercase() }
                            ?.let {
                                expressSubscriberInfo.carrier = it.carrier
                                mongoTemplate.save(expressSubscriberInfo)
                                messageAdapter.sendGroupMessage(
                                    dto.sender.group.id,
                                    "订阅成功! ${carrierInfo[it.carrier]?.nameZhCn} : ${it.number}"
                                )
                            } ?: return@let
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

    fun notify(webHookTracks17: WebHookTracks17) {
        if (webHookTracks17.event == "TRACKING_STOPPED") {
            return
        }
        val find = mongoTemplate.find(
            Query.query(
                Criteria.where("postNumber").`is`(webHookTracks17.data.number.lowercase())
            ),
            ExpressSubscriberInfo::class.java
        )
        if (find.isEmpty()) {
            return
        }
        val first = find.first()
        val oldTrackData = first.trackData
        first.trackData = webHookTracks17.data
        mongoTemplate.save(first)
        oldTrackData?.run {
            if (this.trackInfo.tracking.providersHash == webHookTracks17.data.trackInfo.tracking.providersHash) {
                return
            }
        }
        first.subscriberList.forEach { (groupId) ->
            sendExpressInfo(first, groupId, null)
        }
    }


    //发送快递信息
    fun sendExpressInfo(expressInfo: ExpressSubscriberInfo, groupId: Long?, senderId: Long?) {
        val stringBuilder = StringBuilder()
        val data = expressInfo.trackData ?: return
        val first = data.trackInfo.tracking.providers.first()
        stringBuilder.append(" 单号: ${data.number},${first.provider.name}, 当前状态: ${statusParse(data.trackInfo.latestStatus.status)}")

        if (data.trackInfo.tracking.providers.first().events.size > 0) {
            val timeIso = data.trackInfo.tracking.providers.first().events.last().timeIso
            val startTime = LocalDateTime.parse(timeIso, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val duration: Duration = Duration.between(startTime, LocalDateTime.now())
            stringBuilder.append(", 已耗时${duration.toDays()}天${duration.toHoursPart()}小时${duration.toMinutesPart()}分钟\n")
            val tracking = data.trackInfo.tracking
            val joinToString = tracking.providers.first().events.joinToString("\n") {
                LocalDateTime.parse(
                    it.timeIso,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "  " + it.description.replace(
                    Regex("\\d{11}"),
                    "***********"
                ).replace(Regex("\\d{3}-\\d{8}|\\d{4}-\\{7,8}"), "****-*******")
            }
            stringBuilder.append(joinToString)
            return
        }

        groupId?.let {
            senderId?.let {
                sendExpressInfo(stringBuilder.toString(), groupId, senderId)
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


    private fun statusParse(status: String): String {
        return when (status) {
            "NotFound" -> "查询不到"
            "InfoReceived" -> "收到信息"
            "InTransit" -> "运输途中"
            "Expired" -> "运输过久"
            "AvailableForPickup" -> "到达待取"
            "OutForDelivery" -> "派送途中"
            "DeliveryFailure" -> "投递失败"
            "Delivered" -> "成功签收"
            "Exception" -> "可能异常"
            else -> "未知"
        }
    }


    private fun errorMessageParse(code: Int): String {
        return when (code) {
            0 -> "成功"
            -18010001 -> "IP 地址不在白名单内。"
            -18010002 -> "访问密钥无效。"
            -18010003 -> "内部服务错误，请稍候重试。"
            -18010004 -> "账号被禁用。"
            -18010005 -> "未授权的访问。"
            -18010010 -> "需要提供数据项 {0}。"
            -18010011 -> "数据项 {0} 的值无效。"
            -18010012 -> "数据项 {0} 的格式无效。"
            -18010013 -> "提交数据无效。"
            -18010014 -> "提交物流单号参数超过 40 个最大限制。"
            -18010015 -> "字段 {1} 的值 {0} 无效。"
            -18010016 -> "只有邮政物流才可以设置尾程渠道。"
            -18010204 -> "没有设置WebHook地址，无法推送。"
            -18019901 -> "物流单号 {0} 已经注册过，不需重复注册。"
            -18019902 -> "物流单号 {0} 还未注册，请先进行注册。"
            -18019903 -> "快递公司不能被识别。"
            -18019904 -> "只能重新跟踪已经停止跟踪的物流单号。"
            -18019905 -> "每个物流单号只能被重新跟踪一次。"
            -18019906 -> "只能停止跟踪正在跟踪中的物流单号。"
            -18019907 -> "超出了每天限额。"
            -18019908 -> "额度已经用完。"
            -18019909 -> "暂时没有跟踪信息。"
            -18019910 -> "运输商代码 {0} 不正确。"
            -18019911 -> "运输商暂时不支持注册。"
            -18019801 -> "存在相同单号的多个运输商注册信息, 请明确指定需要变更的现有 运输商代码 carrier_old。"
            -18019802 -> "提交变更的新 运输商代码 carrier_new {0} 可能错误。"
            -18019803 -> "请求变更的 运输商代码 不能相同。"
            -18019804 -> "请求变更的新 运输商代码 carrier_new or final_carrier_new 必须明确指定其一。"
            -18019805 -> "没有注册指定 运输商代码 {0} 的物流单号 {1}，或者现有 运输商代码 carrier_old 错误。"
            -18019806 -> "已经停止跟踪的物流单号不能修改运输商，请激活后再进行修改。"
            -18019807 -> "超出运输商修改次数上限。"
            -18019808 -> "最近注册或修改后还未返回跟踪结果，请等待跟踪结果返回后再进行修改。"
            -18019809 -> "已经存在物流单号的运输商为 {0} 的注册信息，不能修改为重复的注册信息。"
            -18019810 -> "满足修改条件的数据不唯一。"
            -18019811 -> "没有有效的数据修改项。"
            else -> "未知错误"
        }
    }

}