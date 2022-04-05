package com.iguigui.process.dto

data class WeatherResponse (
        var data: WeatherData,
        var status: Int,
        var desc: String
)