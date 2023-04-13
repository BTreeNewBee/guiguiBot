package com.iguigui.process.dto.mercari


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("nextPageToken")
    val nextPageToken: String,
    @SerialName("numFound")
    val numFound: String,
    @SerialName("previousPageToken")
    val previousPageToken: String
)