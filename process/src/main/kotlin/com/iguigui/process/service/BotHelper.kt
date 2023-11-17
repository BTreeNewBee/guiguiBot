package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.entity.mongo.GroupPermission
import com.iguigui.process.handler.MessageDispatcher1
import com.iguigui.process.imagegenerator.GeneratorService
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import com.iguigui.process.qqbot.dto.ImageDTO
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.MemberPermission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap


@Component
class BotHelper {

    @Autowired
    lateinit var messageDispatcher: MessageDispatcher1

    private val configAbleMethodList: List<Method> by lazy {
        messageDispatcher.handlerBeans
            .filter { it.key.getAnnotation(SubscribeBotMessage::class.java).export }
            .keys.toList()
    }

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    val helpCache: MutableMap<Long, Any> = ConcurrentHashMap()

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var generatorService: GeneratorService


    @SubscribeBotMessage("Bot帮助菜单", "帮助", false)
    fun helper(dto: GroupMessagePacketDTO) {
        val contentToString = dto.contentToString()
        if (contentToString != "帮助"
            && contentToString != "菜单"
        ) {
            return
        }

        val find = mongoTemplate.find(
            Query.query(
                Criteria.where("groupId").`is`(dto.sender.group.id)
            ),
            GroupPermission::class.java, "groupPermission"
        )

        val enableFunctionSet = find.firstOrNull()?.permission?.toSet() ?: emptySet()

        helpCache[dto.sender.id] = Any()

        val functionList = configAbleMethodList.sortedBy { it.name }
            .mapIndexed { index, it ->
                Triple(
                    index + 1,
                    it.getAnnotation(SubscribeBotMessage::class.java).functionName,
                    enableFunctionSet.contains(it.name)
                )
            }
            .toList()


        val image = generatorService.generateImage(
            "functionList.html",
            functionList,
            imageHeight = 170 + 40 * functionList.size
        )

        runBlocking {
            messageAdapter.sendGroupMessage(dto.sender.group.id, ImageDTO(path = image.absolutePath))
        }

    }


    @SubscribeBotMessage("启用功能", "启用功能", false)
    fun enableFunction(message: GroupMessagePacketDTO) {
        if (message.sender.permission == MemberPermission.MEMBER) {
            return
        }
        val messageChain = message.contentToString()
        if (messageChain.startsWith("启用")) {
            val permission = messageChain.substring(2)
            val subscribeBotMessage = configAbleMethodList[permission.toInt() - 1]

            mongoTemplate.find(
                Query.query(
                    Criteria.where("groupId").`is`(message.sender.group.id)
                ),
                GroupPermission::class.java, "groupPermission"
            ).firstOrNull()?.let {
                mongoTemplate.updateFirst(
                    Query.query(
                        Criteria.where("groupId").`is`(message.sender.group.id)
                    ),
                    Update.update("permission", it.permission + subscribeBotMessage.name),
                    GroupPermission::class.java, "groupPermission"
                )
                messageAdapter.sendGroupMessage(message.sender.group.id, "启用成功")
            } ?: run {
                mongoTemplate.save(GroupPermission(message.sender.group.id, listOf(subscribeBotMessage.name)))
                messageAdapter.sendGroupMessage(message.sender.group.id, "启用成功")
            }
        }
    }


    @SubscribeBotMessage("禁用功能", "禁用功能", false)
    fun disableFunction(message: GroupMessagePacketDTO) {
        if (message.sender.permission == MemberPermission.MEMBER) {
            return
        }
        val messageChain = message.contentToString()
        if (messageChain.startsWith("禁用")) {
            val permission = messageChain.substring(2)
            val subscribeBotMessage = configAbleMethodList[permission.toInt() - 1]

            mongoTemplate.find(
                Query.query(
                    Criteria.where("groupId").`is`(message.sender.group.id)
                ),
                GroupPermission::class.java, "groupPermission"
            ).firstOrNull()?.let {
                mongoTemplate.updateFirst(
                    Query.query(
                        Criteria.where("groupId").`is`(message.sender.group.id)
                    ),
                    Update.update("permission", it.permission - subscribeBotMessage.name),
                    GroupPermission::class.java, "groupPermission"
                )
                messageAdapter.sendGroupMessage(message.sender.group.id, "禁用成功")
            } ?: run {
                messageAdapter.sendGroupMessage(message.sender.group.id, "禁用失败")
            }
        }
    }

    @SubscribeBotMessage("Bot帮助菜单", "帮助", false)
    fun help0(dto: GroupMessagePacketDTO) {
        if (helpCache[dto.sender.id] == null) {
            return
        }
        val id: Int?
        try {
            id = dto.contentToString().toInt()
        } catch (e: NumberFormatException) {
            return
        }
        helpCache.remove(dto.sender.id)
        configAbleMethodList[id - 1].getAnnotation(SubscribeBotMessage::class.java).let {
            val image = generatorService.generateImage(
                "normalMessage.html",
                it.description
            )

            runBlocking {
                messageAdapter.sendGroupMessage(dto.sender.group.id, ImageDTO(path = image.absolutePath))
//                image.delete()
            }
        }
    }


}