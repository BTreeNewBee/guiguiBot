package com.iguigui.process.service

import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.IMessageDispatcher
import com.iguigui.process.qqbot.MessageAdapter
import com.iguigui.process.qqbot.dto.*
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class Subscriber : IMessageDispatcher {

    val log = LogFactory.getLog(Subscriber::class.java)!!

    @Autowired
    lateinit var messageAdapter: MessageAdapter


    /**
     * Json解析规则，需要注册支持的多态的类
     */
    private val json by lazy {
        Json {
            encodeDefaults = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }


    //一大堆没啥用的搜索辅助
    private val searchHelperMap: Map<String, String> = mapOf(
        "百度" to "https://www.baidu.com/baidu?wd=",
        "谷歌" to "https://www.google.com/search?q=",
        "必应" to "https://cn.bing.com/search?q=",
        "淘宝" to "https://s.taobao.com/search?q=",
        "京东" to "https://search.jd.com/Search?keyword=",
        "微博" to "https://s.weibo.com/weibo?q=",
        "github" to "https://github.com/search?q=",
        "b站" to "https://search.bilibili.com/all?keyword=",
        "不会百度" to "https://buhuibaidu.me/?s=",
    )

    override fun handler(message: DTO) {
        when (message) {
            is GroupMessagePacketDTO -> groupMessageProcessing(message)
        }
    }

    private fun groupMessageProcessing(message: GroupMessagePacketDTO) {
        val toString = message.messageChain.toString()
        messageAdapter.sendGroupMessage(message.sender.group.id, "收到来自 ${message.sender.memberName} 的消息：$toString")
    }

    fun sendMessage(s: String) {
        val groupId = 1L
        log.info("定时任务发送消息：$s")
//        messageAdapter.sendGroupMessage(groupId, s)
    }

}
