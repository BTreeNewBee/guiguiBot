package com.iguigui.process.qqbot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


/**
 * 负责组织转换器、分发器等组件的结构
 * 把消息分发器注册到消息转换器中
 * 当消息转换器收到消息，转换后递交给分发器
 */
@Component
class BotRegister {

    @Autowired
    lateinit var messageAdapt: MessageAdapter

    @Autowired
    lateinit var messageDispatcher: IMessageDispatcher


    @PostConstruct
    fun registerAdapt() {
        messageAdapt.registerHandler(messageDispatcher::handler)
    }


}