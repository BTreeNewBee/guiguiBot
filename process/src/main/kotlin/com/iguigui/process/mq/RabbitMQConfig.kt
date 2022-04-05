package com.iguigui.process.mq

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitMQConfig {

    @Bean
    open fun helloQueue(): Queue {
        return Queue("botinfo")
    }

}