package com.iguigui.qqbot.dto

import kotlinx.serialization.Serializable

@Serializable
data class HolidayInfo(
    val date: Int,
    val workday: Int,
    val holiday: Int,
    val date_cn: String,
    val workday_cn: String,
    val holiday_cn: String
)
