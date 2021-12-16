package com.iguigui.qqbot.util;

import cn.hutool.http.HttpUtil
import com.google.gson.Gson
import com.iguigui.qqbot.dto.Weather
import com.iguigui.qqbot.dto.WeatherResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Component
class MessageUtil {

    fun getWeather(city: String) : String {
        var res = ""
        if (!city.isBlank()) {
            val watherResponse = HttpUtil.get("http://wthrcdn.etouch.cn/weather_mini?city=$city")
            try {
                val weatherResponse: WeatherResponse = Gson().fromJson(watherResponse, WeatherResponse::class.java)
                val today: Weather = weatherResponse.data.forecast[0]
                val tomorrow: Weather = weatherResponse.data.forecast[1]
                today.fengli = today.fengli.replace("<![CDATA[", "").replace("]]>", "")
                res = city + today.type + "，" +
                        today.low + "~" +
                        today.high + "，" +
                        today.fengxiang +
                        today.fengli + "\n明天" +
                        tomorrow.type + "，" +
                        tomorrow.low + "~" +
                        tomorrow.high + "，" +
                        weatherResponse.data.ganmao
            } catch (e: Exception) {
                println(e)
            }
        }
        return res
    }


    fun getLoveWords() : String {
        return HttpUtil.get("https://api.lovelive.tools/api/SweetNothings")
    }

    fun getMoleNotice() :String{
        val stringBuilder = StringBuilder()
        stringBuilder.append("摸鱼小助手提醒您：\n")
        val now = LocalDateTime.now()
        val onWork = now.withHour(9).withMinute(0).withSecond(0)
        val offWork = now.withHour(18).withMinute(30).withSecond(0)
        if (now.dayOfWeek.value < 6 && now.isBefore(offWork) && now.isAfter(onWork)) {
            val duration: Duration = Duration.between(now, offWork)
            stringBuilder.append("距离下班还有${duration.toHoursPart()}小时${duration.toMinutesPart()}分${duration.toSecondsPart()}秒\n")
        } else {
            stringBuilder.append("现在是下班时间\n")
        }

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
        val weather = getWeather("深圳")
        stringBuilder.append(weather)
        return stringBuilder.toString()
    }

    fun getNextYearHoliday() {
        val format = LocalDate.now()
        val url = "https://api.apihubs.cn/holiday/get?field=date,holiday,workday&year=${format.year}&holiday_legal=1&size=365"
        val get = HttpUtil.get(url)
        val parseToJsonElement = Json.parseToJsonElement(get)


    }

}
