package com.iguigui.dto

@kotlinx.serialization.Serializable
data class HeartBeat(
    val key: String,
    val rate: Long)
