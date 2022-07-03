package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("city")
    val city: String?,
    @SerialName("coordinates")
    val coordinates: Coordinates?,
    @SerialName("country")
    val country: String?,
    @SerialName("postal_code")
    val postalCode: String?,
    @SerialName("state")
    val state: String?,
    @SerialName("street")
    val street: String?
)