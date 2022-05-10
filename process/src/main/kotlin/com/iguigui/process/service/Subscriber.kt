package com.iguigui.process.service

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Member
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.util.*

@Component
class Subscriber {

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    @SubscribeBotMessage
    fun searchHelper(dto: GroupMessagePacketDTO) {
        println(dto.contentToString())
        searchHelperMap.entries.forEach {
//            if (dto.lowercase(Locale.getDefault()).startsWith(it.key)) {
//                val substring = contentToString.substring(2)
//                if (substring.trim().isNotEmpty()) {
//                    runBlocking {
//                        sender.group.sendMessage(
//                            "这也要我教？？？自己去看\n${
//                                it.value + URLEncoder.encode(
//                                    substring.trim(),
//                                    "UTF-8"
//                                )
//                            }"
//                        )
//                    }
//                }
//            }
        }
    }

    @SubscribeBotMessage
    fun memberListEvent(dto: MemberListData) {
        println("memberListEvent DTO = ${dto}")
    }


    @SubscribeBotMessage
    fun memberCardChangeEvent(dto: MemberCardChangeEventDTO) {
        val group = dto.member.group
        messageAdapter.sendGroupMessage(group.id, "有人改名字了我不说是谁")
        println("memberListEvent DTO = ${dto}")
    }


    private val searchHelperMap: Map<String, String> = mapOf(
        "百度" to "https://www.baidu.com/baidu?wd=",
        "谷歌" to "https://www.google.com/search?q=",
        "必应" to "https://cn.bing.com/search?q=",
        "淘宝" to "https://s.taobao.com/search?q=",
        "github" to "https://github.com/search?q=",
        "b站" to "https://search.bilibili.com/all?keyword=",
    )


}

fun GroupMessagePacketDTO.contentToString(): String {
    return this.messageChain.joinToString(""){it.toString()}
}