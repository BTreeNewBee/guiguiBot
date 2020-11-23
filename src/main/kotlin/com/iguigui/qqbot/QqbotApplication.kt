package com.iguigui.qqbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class QqbotApplication

fun main(args: Array<String>) {
	runApplication<QqbotApplication>(*args)
}
