package com.iguigui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
open class QqbotApplication

fun main(args: Array<String>) {
	runApplication<QqbotApplication>(*args)
}
