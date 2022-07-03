package com.iguigui.process.dto.tracks17


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiscInfo(
    @SerialName("customer_number")
    val customerNumber: String?,
    @SerialName("dimensions")
    val dimensions: String?,
    @SerialName("local_key")
    val localKey: Int?,
    @SerialName("local_number")
    val localNumber: String?,
    @SerialName("local_provider")
    val localProvider: String?,
    @SerialName("pieces")
    val pieces: String?,
    @SerialName("reference_number")
    val referenceNumber: String?,
    @SerialName("risk_factor")
    val riskFactor: Int?,
    @SerialName("service_type")
    val serviceType: String?,
    @SerialName("weight_kg")
    val weightKg: String?,
    @SerialName("weight_raw")
    val weightRaw: String?
)