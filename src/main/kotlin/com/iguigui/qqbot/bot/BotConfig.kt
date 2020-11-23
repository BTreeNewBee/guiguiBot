package com.iguigui.qqbot.bot

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@Component
class BotConfig {

    @Value("\${qq.id}")
    lateinit var qqId: String

    @Value("\${qq.password}")
    lateinit var qqPassword: String

    @Bean
    fun getBot(): Bot {

        var bot: Bot
        runBlocking {
            bot = Bot( // JVM 下也可以不写 `QQAndroid.` 引用顶层函数
                    qqId.toLong(),
                    qqPassword
            ) {
                fileBasedDeviceInfo("device.json") // 使用 "device.json" 保存设备信息
                // networkLoggerSupplier = { SilentLogger } // 禁用网络层输出
            }.alsoLogin()
        }
        return bot
    }

}