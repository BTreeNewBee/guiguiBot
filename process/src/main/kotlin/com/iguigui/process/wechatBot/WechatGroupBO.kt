package com.iguigui.bot.wechatBot

import com.iguigui.bot.wechatBot.Constant.Companion.CHATROOM_MEMBER
import com.iguigui.bot.wechatBot.Constant.Companion.CHATROOM_MEMBER_NICK
import com.iguigui.bot.wechatBot.Constant.Companion.TXT_MSG
import com.iguigui.bot.wechatBot.dto.SendTextMessageDTO
import com.iguigui.process.botInterface.Group
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WechatGroupBO : Group {

    var bot : WechatBot? = null

    var groupID : String? = null

    override fun getMemberlist() {
        TODO("Not yet implemented")
    }

    override fun sendTextMessage(content: String) {
        this.getId()?.let {
            val sendTextMessageDTO = SendTextMessageDTO(
                it,
                content,
                "null",
                "null",
                "null",
                type = TXT_MSG,
                id = System.currentTimeMillis().toString()
            )
            bot?.send(Json.encodeToString(sendTextMessageDTO))
        }
    }

    override fun getId(): String? {
        return groupID
    }

    override fun setId(id : String) {
        this.groupID = id
    }

    override fun syncGroupMember() {
        this.getId()?.let {
            val sendTextMessageDTO = SendTextMessageDTO(
                it,
                "null",
                "null",
                "null",
                "null",
                type = CHATROOM_MEMBER,
                id = System.currentTimeMillis().toString()
            )
            bot?.send(Json.encodeToString(sendTextMessageDTO))
        }
    }

    override fun syncGroupMemberNick(memberWxId: String) {
        this.getId()?.let { groupID ->
            val sendTextMessageDTO = SendTextMessageDTO(
                memberWxId,
                "null",
                groupID,
                "null",
                "null",
                type = CHATROOM_MEMBER_NICK,
                id = System.currentTimeMillis().toString()
            )
            bot?.send(Json.encodeToString(sendTextMessageDTO))
        }
    }

    fun setWechat(bot : WechatBot) {
        this.bot = bot
    }


}