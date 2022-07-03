package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShippingInfo(
    @SerialName("recipient_address")
    val recipientAddress: Address,
    @SerialName("shipper_address")
    val shipperAddress: Address
)