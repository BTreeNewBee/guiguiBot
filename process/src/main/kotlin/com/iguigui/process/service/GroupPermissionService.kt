package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.entity.mongo.GroupPermission
import com.iguigui.process.handler.MessageDispatcher1
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.UpdateDefinition
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct


@Component
class GroupPermissionService {

    @Autowired
    lateinit var messageDispatcher: MessageDispatcher1

    private lateinit var methodList: List<SubscribeBotMessage>

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @Autowired
    lateinit var mongoTemplate: MongoTemplate


    val helpCache: MutableMap<Long, Any> = ConcurrentHashMap()

    @PostConstruct
    fun initMethodCache() {
        methodList =
            messageDispatcher.handlerBeans.map { it.key.getAnnotation(SubscribeBotMessage::class.java) }.toList()
    }



    fun checkGroupPermission(groupId: Long, permission: String): Boolean {
        val groupPermission = getGroupPermission(groupId)
        return groupPermission?.permission?.contains(permission) ?: false
    }


    private fun getGroupPermission(groupId: Long): GroupPermission? {
        return mongoTemplate.find(
            Query.query(
                Criteria.where("groupId").`is`(groupId)
            ),
            GroupPermission::class.java, "groupPermission"
        ).firstOrNull()
    }


}