package com.iguigui.qqbot.bot.wechatBot

import com.iguigui.qqbot.bot.Group
import com.iguigui.qqbot.bot.wechatBot.Constant.Companion.TXT_MSG
import com.iguigui.qqbot.bot.wechatBot.dto.SendTextMessageDTO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WechatGroup : Group{

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

    fun setWechat(bot : WechatBot) {
        this.bot = bot
    }


}