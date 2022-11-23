package com.iguigui.process.handler

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.common.interfaces.DTO
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import javax.annotation.PostConstruct
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations

@Component
class MessageDispatcher1 : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    private val messageHandlers = mutableMapOf<KClass<out DTO>, MutableList<Method>>()

    //Inject the application context
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }


    //Register the message handler
    @PostConstruct
    fun init() {
        //Get all the beans
        val beans = applicationContext.getBeansOfType(Object::class.java)
        beans.forEach { entry ->
            val kClass = entry.value::class
//            println(kClass)
//            println(kClass.functions.isEmpty())
            entry.value.`class`.methods.forEach { method ->
                    registerHandler(method)
            }
//            kClass.functions.forEach {
//                registerHandler(it)
//            }
        }
    }


    private fun registerHandler(method: Method) {
        val findAnnotations = method.getAnnotationsByType(SubscribeBotMessage::class.java)
        if (findAnnotations.isNotEmpty()) {
            return
        }
        val subscribeBotMessage = findAnnotations.first()
        if (!subscribeBotMessage.isFunction) {
            return
        }


    }

}