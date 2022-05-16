package com.iguigui.process.util;

import cn.hutool.http.HttpUtil
import com.google.gson.Gson
import com.iguigui.process.dto.HolidayInfo
import com.iguigui.process.dto.Weather
import com.iguigui.process.dto.WeatherResponse
import kotlinx.serialization.json.*
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

@Service
@Component
class MessageUtil {

    fun getWeather(city: String) : String {
        var res = ""
//        if (!city.isBlank()) {
//            val watherResponse = HttpUtil.get("http://wthrcdn.etouch.cn/weather_mini?city=$city")
//            try {
//                val weatherResponse: WeatherResponse = Gson().fromJson(watherResponse, WeatherResponse::class.java)
//                val today: Weather = weatherResponse.data.forecast[0]
//                val tomorrow: Weather = weatherResponse.data.forecast[1]
//                today.fengli = today.fengli.replace("<![CDATA[", "").replace("]]>", "")
//                res = city + today.type + "，" +
//                        today.low + "~" +
//                        today.high + "，" +
//                        today.fengxiang +
//                        today.fengli + "\n明天" +
//                        tomorrow.type + "，" +
//                        tomorrow.low + "~" +
//                        tomorrow.high + "，" +
//                        weatherResponse.data.ganmao
//            } catch (e: Exception) {
//                println(e)
//            }
//        }
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
//        if (now.dayOfWeek.value < 6 && now.isBefore(offWork) && now.isAfter(onWork)) {
//            val duration: Duration = Duration.between(now, offWork)
//            stringBuilder.append("距离下班还有${duration.toHoursPart()}小时${duration.toMinutesPart()}分${duration.toSecondsPart()}秒\n")
//        } else {
//            stringBuilder.append("现在是下班时间\n")
//        }

        val now1 = LocalDate.now()
        stringBuilder.append("今天是${now1.format(DateTimeFormatter.ISO_LOCAL_DATE)}日，今年的第${now1.dayOfYear}天，剩余${now1.lengthOfYear() - now1.dayOfYear}天\n")

        holidayInfo.forEach {
            val date = LocalDate.parse(it.date.toString(), DateTimeFormatter.BASIC_ISO_DATE)
            if (date.isEqual(now1)) {
                stringBuilder.append("${it.holiday_cn}到了，我宣布全群放假！\n")
            } else {
                val toDays = now1.until(date,ChronoUnit.DAYS)
                if (toDays < 355) {
                    stringBuilder.append("距离${it.holiday_cn}还有${toDays}天\n")
                }
            }
        }
        val weather = getWeather("深圳")
        stringBuilder.append(weather)
        return stringBuilder.toString()
    }

    var holidayInfo : MutableList<HolidayInfo> = mutableListOf()

    @PostConstruct
    fun getNextYearHoliday() {
        var holidayInfo : MutableList<HolidayInfo> = mutableListOf()

        val mutableListOf = mutableListOf<HolidayInfo>()
        val now = LocalDate.now()
        val holidayInfo1 = getHolidayInfo(now.year.toString())
        holidayInfo1?.let {
            mutableListOf.addAll(Json.decodeFromJsonElement(it))
        }
        val holidayInfo2 = getHolidayInfo(now.plusYears(1).year.toString())
        holidayInfo2?.let {
            mutableListOf.addAll(Json.decodeFromJsonElement(it))
        }
        mutableListOf.sortBy { it.date }


        var holidayInfoSet : MutableSet<String> = mutableSetOf()
        mutableListOf.forEach {
            val parse = LocalDate.parse(it.date.toString(), DateTimeFormatter.BASIC_ISO_DATE)
            if (parse.isBefore(now)) {
                return@forEach
            }
            if (now.isEqual(parse)) {
                println("yes data equals ! ${it}")
            }

            if (holidayInfoSet.contains(it.holiday_cn)) {
                return@forEach
            }
            holidayInfoSet.add(it.holiday_cn)
            holidayInfo.add(it)
        }
        this.holidayInfo = holidayInfo
    }

    /**
     * 获取从今天开始往后的一年的节假日
     */
    fun getHolidayInfo(year : String) :JsonArray? {
        val url = "https://api.apihubs.cn/holiday/get?field=date,holiday,workday&year=$year&holiday_legal=1&size=365&cn=1"
        val get = HttpUtil.get(url)
        val dataList = Json.parseToJsonElement(get).jsonObject["data"]?.jsonObject?.get("list")?.jsonArray
        return dataList
    }

}
