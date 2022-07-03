package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeMetrics(
    @SerialName("days_after_last_update")
    val daysAfterLastUpdate: Int,
    @SerialName("days_after_order")
    val daysAfterOrder: Int,
    @SerialName("days_of_transit")
    val daysOfTransit: Int,
    @SerialName("days_of_transit_done")
    val daysOfTransitDone: Int,
    @SerialName("estimated_delivery_date")
    val estimatedDeliveryDate: EstimatedDeliveryDate
)