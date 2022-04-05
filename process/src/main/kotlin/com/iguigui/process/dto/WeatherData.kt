package com.iguigui.process.dto


data class WeatherData (
        var yesterday: Weather,
        var city: String,
        var forecast: List<Weather>,
        var ganmao: String,
        var wendu: Int
)