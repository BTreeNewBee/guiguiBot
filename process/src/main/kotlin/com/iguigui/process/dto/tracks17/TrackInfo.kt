package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackInfo(
    @SerialName("latest_event")
    val latestEvent: LatestEvent?,
    @SerialName("latest_status")
    val latestStatus: LatestStatus,
    @SerialName("milestone")
    val milestone: List<Milestone>,
    @SerialName("misc_info")
    val miscInfo: MiscInfo,
    @SerialName("shipping_info")
    val shippingInfo: ShippingInfo,
    @SerialName("time_metrics")
    val timeMetrics: TimeMetrics,
    @SerialName("tracking")
    val tracking: Tracking
)