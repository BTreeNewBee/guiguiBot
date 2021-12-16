package com.iguigui.qqbot.service.impl

import com.iguigui.qqbot.bot.wechatBot.Constant
import com.iguigui.qqbot.bot.wechatBot.WechatBot
import com.iguigui.qqbot.bot.wechatBot.WechatGroup
import com.iguigui.qqbot.bot.wechatBot.dto.RecverTextMessageDTO
import com.iguigui.qqbot.bot.wechatBot.dto.UserListDTO
import com.iguigui.qqbot.service.WechatMessageService
import com.iguigui.qqbot.util.MessageUtil
import kotlinx.serialization.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WechatMessageServiceImpl : WechatMessageService {

    @Autowired
    lateinit var wechatBot: WechatBot

    @Autowired
    lateinit var messageUtil: MessageUtil

    override fun processMessage(message: String) {
        if (message == "null") {
            return
        }
        val parseToJsonElement = Json.parseToJsonElement(message)
        val type = parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int
        if (type != Constant.HEART_BEAT) {
            println(parseToJsonElement)
        }
        when (parseToJsonElement.jsonObject["type"]?.jsonPrimitive?.int) {
            Constant.HEART_BEAT -> {
            }
            Constant.GET_USER_LIST_SUCCSESS -> {
                println("GET_USER_LIST_SUCCSESS")
            }
            Constant.USER_LIST -> {
                val userListDTO = Json.decodeFromJsonElement<UserListDTO>(parseToJsonElement)
                userListDTO.content?.forEach { content ->
                    content.wxid?.let { wxid ->
                        if (wxid.endsWith("@chatroom")) {
                            val wechatGroup = WechatGroup()
                            wechatGroup.setId(wxid)
                            wechatGroup.setWechat(wechatBot)
                            wechatBot.addGroup(wechatGroup)
                        }
                    }
                }
            }
            Constant.RECV_PIC_MSG -> {
                println("RECV_PIC_MSG")
            }
            Constant.RECV_TXT_MSG -> {
                println("RECV_TXT_MSG")
                val recverTextMessageDTO = Json.decodeFromJsonElement<RecverTextMessageDTO>(parseToJsonElement)
                recverTextMessageDTO.content?.contains("摸鱼")?.let { mole ->
                    if (mole) {
                        recverTextMessageDTO.wxid?.let {
                            wechatBot.getGroupById(it)?.sendTextMessage(messageUtil.getMoleNotice())
                        }
                    }
                }
            }
            Constant.RECV_XML_MSG -> {
                println("RECV_XML_MSG")
            }
            Constant.GET_USER_LIST_FAIL -> {
                println("GET_USER_LIST_FAIL")
            }
            Constant.CHATROOM_MEMBER -> {
                println("CHATROOM_MEMBER")
            }
            Constant.CHATROOM_MEMBER_NICK -> {
                println("CHATROOM_MEMBER_NICK")
            }
            Constant.PERSONAL_INFO -> {
                println("PERSONAL_INFO")
            }
            Constant.DEBUG_SWITCH -> {
                println("DEBUG_SWITCH")
            }
            Constant.PERSONAL_DETAIL -> {
                println("PERSONAL_DETAIL")
            }
            Constant.DESTROY_ALL -> {
                println("DESTROY_ALL")
            }
            Constant.NEW_FRIEND_REQUEST -> {
                println("NEW_FRIEND_REQUEST")
            }
            Constant.AGREE_TO_FRIEND_REQUEST -> {
                println("AGREE_TO_FRIEND_REQUEST")
            }

        }

    }
}