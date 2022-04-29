package com.iguigui.common.annotations

import com.iguigui.common.interfaces.DTO
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
