package com.iguigui.qqbot.config

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import javax.crypto.Cipher.SECRET_KEY

import com.baidu.aip.ocr.AipOcr




@Component
class BaiduOcr {

    private val appId = "21306061"
    private val apiKey = "AWCPVSe8nmxCvQSphD3fYgDZ"
    private val secretKey = "8WkYOFPmZE5rQXjR1uXDnAih6YOHonZt"

    @Bean
    fun getApi(): AipOcr {
        return AipOcr(appId, apiKey, secretKey)
    }

}