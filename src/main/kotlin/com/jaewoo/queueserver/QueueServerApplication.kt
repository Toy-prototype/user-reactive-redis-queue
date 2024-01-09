package com.jaewoo.queueserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class QueueServerApplication {
}

fun main(args: Array<String>) {
	runApplication<QueueServerApplication>(*args)
}
