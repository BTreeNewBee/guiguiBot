package com.iguigui.qqbot.dto

data class WeatherResponse (
        var data: WeatherData,
        var status: Int,
        var desc: String
)