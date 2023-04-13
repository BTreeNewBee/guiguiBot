package com.iguigui.process.dto.mercari


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemBrand(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("subName")
    val subName: String
)