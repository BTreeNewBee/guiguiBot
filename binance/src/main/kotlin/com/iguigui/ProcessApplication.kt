package com.iguigui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class ProcessApplication

fun main(args: Array<String>) {
	runApplication<ProcessApplication>(*args)
}
