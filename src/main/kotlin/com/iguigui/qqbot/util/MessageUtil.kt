package com.iguigui.qqbot.util;

import cn.hutool.http.HttpUtil
import com.google.gson.Gson
import com.iguigui.qqbot.dto.Weather
import com.iguigui.qqbot.dto.WeatherResponse
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.LocalDate
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
                val date: LocalDate = LocalDate.now()
                val myDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月")
                res = "今天是" + myDateTimeFormatter.format(date) +
                        today.date + "，" +
                        city + today.type + "，" +
                        today.low + "~" +
                        today.high + "，" +
                        today.fengxiang +
                        today.fengli + "，明天" +
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

}
