package com.iguigui.process.entity

import com.iguigui.process.express.dto.ExoressData
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "Girl")
data class GirlInfo(
    @MongoId
    var _id : ObjectId?,
    var data: MutableList<String>
)
