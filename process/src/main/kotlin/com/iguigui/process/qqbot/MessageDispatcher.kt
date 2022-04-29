package com.iguigui.process.qqbot

import com.iguigui.common.interfaces.DTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class MessageDispatcher {


    /**
     * 扫描订阅方法，注册到
      */
    @PostConstruct
    fun registerSubscriber() {

    }


    fun handler(message: DTO){

    }

    private fun dispatcherMessage(message: DTO) {

    }

}