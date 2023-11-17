package com.iguigui.process.annotations

import com.iguigui.process.annotations.messageFilter.GroupMessageFilter
//import net.mamoe.mirai.contact.MemberPermission
import kotlin.reflect.KClass

/**
 * 用于方法订阅消息的注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubscribeBotMessage(
    val functionName: String, //功能名称
    val description: String = "", //功能描述
    val export: Boolean = false, //是否暴露出来可配置，暴露出来都需要权限
//    val priority: Int = 10, //优先级
//    val memberPermission: MemberPermission = MemberPermission.MEMBER, //用户调度权限
    val filter: Array<KClass<out GroupMessageFilter>> = [] //过滤器
)
