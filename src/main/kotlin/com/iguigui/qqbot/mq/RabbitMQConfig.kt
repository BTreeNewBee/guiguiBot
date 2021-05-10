package com.iguigui.qqbot.mq

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun  helloQueue(): Queue {
        return Queue("botinfo")
    }

}