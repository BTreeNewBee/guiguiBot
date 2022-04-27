package com.iguigui.process.annotations

import com.iguigui.process.qqbot.dto.DTO
import kotlin.reflect.KClass

/**
 * 用于方法订阅消息的注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SubscribeBotMessage(
    val clazz:KClass<out DTO>,
    val priority:Int = 10
)
