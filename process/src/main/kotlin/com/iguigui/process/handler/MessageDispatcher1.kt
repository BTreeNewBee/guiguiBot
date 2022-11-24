package com.iguigui.process.handler

import com.iguigui.common.annotations.SubscribeBotMessage
import com.iguigui.common.interfaces.DTO
import com.iguigui.process.qqbot.IMessageDispatcher
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import javax.annotation.PostConstruct
import kotlin.reflect.KClass

@Component
class MessageDispatcher1 : ApplicationContextAware, IMessageDispatcher {

    private lateinit var applicationContext: ApplicationContext

    private val messageHandlers = mutableMapOf<KClass<out DTO>, MutableList<Method>>()

    private val handlerBeans = mutableMapOf<Method, Any>()

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
            entry.value.`class`.methods.forEach { method ->
                registerHandler(entry.value, method)
            }
        }
    }


    private fun registerHandler(bean: Any, method: Method) {
        val findAnnotations = method.getAnnotationsByType(SubscribeBotMessage::class.java)
        if (findAnnotations.isEmpty()) {
            return
        }
        val subscribeBotMessage = findAnnotations.first()
        if (!subscribeBotMessage.isFunction) {
            return
        }
        val parameters = method.parameters
        if (parameters.size > 1) {
            return
        }
        val parameter = parameters[0]
        val assignableFrom = DTO::class.java.isAssignableFrom(parameter.type)
        if (assignableFrom) {
            messageHandlers.getOrPut(parameter.type.kotlin as KClass<out DTO>) { ArrayList() }.add(method)
            handlerBeans[method] = bean
        }
    }

    //Dispatch the message
    override fun handler(message: DTO) {
        messageHandlers[message::class]?.forEach { method ->
//            method(handlerBeans[method], message)
            method.invoke(handlerBeans[method], message)
        }
    }


}