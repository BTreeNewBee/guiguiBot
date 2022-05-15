package com.iguigui.process.express

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import com.iguigui.process.express.dto.ExpressResult
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern

@Component
class ExpressUtil {

    private var tokenV2 = "hY2tuh9UUKy_uY5gryWLA2pdvRH_Rb4s0eWAyqOvTjALAEGeOd1dXlpSm7N3OGdC"

    private var cookie = "BAIDUID=C5B2147727C4AA533DF7BD7AA800292E:FG=1"

    private var pattern = Pattern.compile("tokenV2=([\\w-]{64})")

    private var json = Json{
        ignoreUnknownKeys = true
    }

    //定期刷新cookie
    @Scheduled(fixedDelay = 3600 * 1000 * 8)
    fun refreshCookieToken() {
        val token =
            HttpRequest.get("https://www.baidu.com/baidu?isource=infinity&iname=baidu&itype=web&tn=02003390_42_hao_pg&ie=utf-8&wd=%E5%BF%AB%E9%80%92")
                .execute()
        val headers = token.headers()
        val matcher = pattern.matcher(token.body())
        if (matcher.find()) {
            val group = matcher.group()
            val split = group.split("=")
            tokenV2 = split[1]
            if (split[1].length != 64) {
                println(token.body())
                throw RuntimeException("百度tokenV2获取失败 $tokenV2")
            }
        } else {
            throw RuntimeException("百度tokenV2获取失败")
        }

        val first: Optional<MutableMap.MutableEntry<String, MutableList<String>>> =
            headers.entries.stream().filter { (key): Map.Entry<String, List<String>> -> "Set-Cookie" == key }
                .findFirst()
        for (s in first.get().value) {
            if (s.startsWith("BAIDUID")) {
                val split = s.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                cookie = split[0]
            }
        }
    }


    fun getExpressInfo(postNumber: String): ExpressResult? {
        val url = "https://express.baidu.com/express/api/express"
        val parameter = HashMap<String, Any>()
        parameter["query_from_srcid"] = "4001"
        parameter["isBaiduBoxApp"] = 10002
        parameter["isWisePc"] = 10020
        parameter["tokenV2"] = tokenV2
        parameter["appid"] = 4001
        parameter["nu"] = postNumber
        parameter["qid"] = "ad1339960007131e"
        parameter["_"] = System.currentTimeMillis()
        val execute = HttpUtil.createGet(url)
            .form(parameter)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:100.0) Gecko/20100101 Firefox/100.0")
            .header("Cookie", cookie)
            .header("Referer", "https://www.baidu.com/baidu?tn=monline_4_dg&ie=utf-8&wd=" + postNumber)
            .execute()
        val body = execute.body()
        val parseToJsonElement = Json.parseToJsonElement(body)
        val content = parseToJsonElement?.jsonObject["status"]?.jsonPrimitive?.content.toString()
        if ("0" != content) {
            return null
        }
        val decodeFromString = json.decodeFromString(ExpressResult.serializer(), body)
        return decodeFromString
    }


}