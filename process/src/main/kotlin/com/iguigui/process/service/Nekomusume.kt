package com.iguigui.process.service

import com.iguigui.process.annotations.SubscribeBotMessage
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.GroupMessagePacketDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


//变猫娘
@Component
class Nekomusume {

    @Autowired
    lateinit var messageAdapter: MessageAdapter

    @SubscribeBotMessage(functionName = "猫娘模式", export = true, description = "开启猫娘命令：变猫娘\n关闭猫娘命令：收收味")
    fun nekomusume(dto: GroupMessagePacketDTO) {

    }


    @SubscribeBotMessage(functionName = "收收味")
    fun showshowway(dto: GroupMessagePacketDTO) {

    }


}