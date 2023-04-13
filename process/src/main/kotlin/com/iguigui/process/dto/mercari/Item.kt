package com.iguigui.process.dto.mercari


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("buyerId")
    val buyerId: String,
    @SerialName("created")
    val created: String,
    @SerialName("id")
    val id: String,
    @SerialName("itemBrand")
    val itemBrand: ItemBrand?,
    @SerialName("itemConditionId")
    val itemConditionId: String,
    @SerialName("itemPromotions")
    val itemPromotions: List<String?>,
    @SerialName("itemSize")
    val itemSize: ItemSize?,
    @SerialName("itemSizes")
    val itemSizes: List<ItemSize>,
    @SerialName("itemType")
    val itemType: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: String,
    @SerialName("sellerId")
    val sellerId: String,
    @SerialName("shippingPayerId")
    val shippingPayerId: String,
    @SerialName("shopName")
    val shopName: String,
    @SerialName("status")
    val status: String,
    @SerialName("thumbnails")
    val thumbnails: List<String>,
    @SerialName("updated")
    val updated: String
)