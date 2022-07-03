package com.iguigui.process.entity

import com.iguigui.process.dto.tracks17.TrackData
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "expressSubscriber")
data class ExpressSubscriberInfo(
    @MongoId
    var _id: ObjectId?,
    var postNumber: String,
    var carrier: Int?,
//    var exoressData: ExoressData,
    var trackData: TrackData?,
    //groupid subscriberid
    var subscriberList: HashMap<Long, ArrayList<Long>>
)
