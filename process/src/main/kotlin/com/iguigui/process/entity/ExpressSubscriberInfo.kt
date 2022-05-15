package com.iguigui.process.entity

import com.iguigui.process.express.dto.ExoressData
import kotlinx.serialization.Serializable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "expressSubscriber")
@Serializable
data class ExpressSubscriberInfo(
    @MongoId
    var _id :String?,
    var postNumber: String,
    var exoressData: ExoressData,
    //groupid subscriberid
    var subscriberList: MutableMap<Long,MutableList<Long>>
)
