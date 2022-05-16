package com.iguigui.process.entity

import com.iguigui.process.express.dto.ExoressData
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "expressSubscriber")
data class ExpressSubscriberInfo(
    @MongoId
    var _id : ObjectId?,
    var postNumber: String,
    var exoressData: ExoressData,
    //groupid subscriberid
    var subscriberList: MutableMap<Long,MutableList<Long>>
)
