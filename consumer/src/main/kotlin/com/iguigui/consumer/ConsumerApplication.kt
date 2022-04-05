package com.iguigui.consumer

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class ConsumerApplication

fun main(args: Array<String>) {
	runApplication<ConsumerApplication>(*args)
	System.`in`.read()
}
