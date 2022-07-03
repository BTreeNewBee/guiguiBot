package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Provider(
    @SerialName("events")
    val events: List<Event>,
    @SerialName("events_hash")
    val eventsHash: Int,
    @SerialName("latest_sync_status")
    val latestSyncStatus: String,
    @SerialName("latest_sync_time")
    val latestSyncTime: String,
    @SerialName("provider")
    val provider: ProviderX,
    @SerialName("service_type")
    val serviceType: String?
)