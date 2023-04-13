package com.iguigui.process.dto.mercari


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemSize(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String
)