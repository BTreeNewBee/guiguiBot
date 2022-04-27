package com.iguigui.process.annotations

/**
 * 用于方法订阅消息的注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SubscribeBotMessage()
