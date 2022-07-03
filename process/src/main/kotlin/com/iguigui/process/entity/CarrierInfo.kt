package com.iguigui.process.entity

import com.iguigui.process.express.dto.ExoressData
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "track17carrier")
data class CarrierInfo(
    @MongoId
    var _id : ObjectId?,
    var data: MutableList<Carrier>
)
