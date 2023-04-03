package com.iguigui.common.annotations

/**
 * 用于方法订阅消息的注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeBotMessage(
    val name:String, //功能名称
    val description:String = "", //功能描述
    val export: Boolean = false,
    val priority:Int = 10
)
