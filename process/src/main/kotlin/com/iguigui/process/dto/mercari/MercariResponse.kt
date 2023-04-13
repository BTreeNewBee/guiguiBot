package com.iguigui.process.dto.mercari


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MercariResponse(
    @SerialName("components")
    val components: List<Component>,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("meta")
    val meta: Meta
)