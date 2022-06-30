package com.iguigui.process.express

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import com.iguigui.process.express.dto.ExpressResult
import com.iguigui.process.service.Subscriber
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.commons.logging.LogFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern
import javax.annotation.PostConstruct

@Component
class ExpressUtil {

    val log = LogFactory.getLog(ExpressUtil::class.java)!!

    private var tokenV2 = "hY2tuh9UUKy_uY5gryWLA2pdvRH_Rb4s0eWAyqOvTjALAEGeOd1dXlpSm7N3OGdC"

    private var qid = "eeb6e34600013f81"

    private var cookie = "BAIDUID=C5B2147727C4AA533DF7BD7AA800292E:FG=1"

    private var patternTokenV2 = Pattern.compile("tokenV2=([\\w-]{64})")

    private var patternQid = Pattern.compile("\"qid\":\"([\\w]{16})")

    private var json = Json{
        ignoreUnknownKeys = true
    }


    @PostConstruct
    fun initParameter() {
        refreshCookieToken()
    }

    //定期刷新cookie
    @Scheduled(fixedDelay = 60 * 1000 * 20)
    fun refreshCookieToken() {
        val token =
            HttpRequest.get("https://www.baidu.com/baidu?isource=infinity&iname=baidu&itype=web&tn=02003390_42_hao_pg&ie=utf-8&wd=%E5%BF%AB%E9%80%92")
                .execute()
        val headers = token.headers()
        cookie = token.cookies.joinToString(";") { it.name + "=" + it.value }
        var matcher = patternTokenV2.matcher(token.body())
        if (matcher.find()) {
            val group = matcher.group()
            val split = group.split("=")
            tokenV2 = split[1]
            if (split[1].length != 64) {
                throw RuntimeException("百度tokenV2获取失败 $tokenV2")
            }
        } else {
            throw RuntimeException("百度tokenV2获取失败")
        }

        matcher = patternQid.matcher(token.body())
        if (matcher.find()) {
            val group = matcher.group()
            val split = group.split(":\"")
            qid = split[1]
            if (split[1].length != 16) {
                throw RuntimeException("百度tokenV2获取失败 $tokenV2")
            }
        } else {
            throw RuntimeException("百度tokenV2获取失败")
        }
        log.info("refreshCookieToken $cookie $tokenV2 $qid")
    }


    //白嫖的百度快递接口，顺丰不能查，以后拉黑了不发顺丰了
    fun getExpressInfo(postNumber: String): ExpressResult? {
        val url = "https://express.baidu.com/express/api/express"
        val parameter = HashMap<String, Any>()
        parameter["query_from_srcid"] = "4001"
        parameter["isBaiduBoxApp"] = 10002
        parameter["isWisePc"] = 10020
        parameter["tokenV2"] = tokenV2
        parameter["appid"] = 4001
        parameter["nu"] = postNumber
        parameter["qid"] = qid
        parameter["vcode"] = ""
        parameter["token"] = ""
        parameter["_"] = System.currentTimeMillis()

        log.info("query param $parameter")

        val execute = HttpUtil.createGet(url)
            .form(parameter)
            .header("Host", "express.baidu.com")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:101.0) Gecko/20100101 Firefox/101.0")
            .header("Accept", "*/*")
            .header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Referer", "https://www.baidu.com/baidu?tn=monline_4_dg&ie=utf-8&wd=$postNumber")
            .header("Connection", "keep-alive")
            .header("Cookie", cookie)
            .header("Sec-Fetch-Dest", "script")
            .header("Sec-Fetch-Mode", "no-cors")
            .header("Sec-Fetch-Site", "same-site")
            .header("Pragma", "no-cache")
            .header("Cache-Control", "no-cache")
            .execute()
        val body = execute.body()
        println("express body $body")
        val parseToJsonElement = Json.parseToJsonElement(body)
        val content = parseToJsonElement?.jsonObject["status"]?.jsonPrimitive?.content.toString()
        if ("0" != content) {
            return null
        }
        val decodeFromString = json.decodeFromString(ExpressResult.serializer(), body)
        return decodeFromString
    }


}