package com.iguigui.process.qqbot

import com.iguigui.common.interfaces.DTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
interface IMessageDispatcher {

    fun handler(message: DTO)


}