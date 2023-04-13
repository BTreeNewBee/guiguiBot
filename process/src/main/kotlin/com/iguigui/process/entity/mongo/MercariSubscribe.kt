package com.iguigui.process.entity.mongo

import com.iguigui.process.dto.mercari.Item
import org.springframework.data.annotation.Id

//群ID-用户ID-关键字
data class MercariSubscribe(
    @Id
    val id: String?,
    val groupId: Long,
    val subscribe: ArrayList<MercariSubscribeInfo>)

data class MercariSubscribeInfo(val userId: Long, val keyword: String, val condition: String, val priceRange: String)



//爬下来的需要存储的信息
data class MercariSpiderContent(
    @Id
    val id: String?,
    val key: String,
    var items: List<Item>)