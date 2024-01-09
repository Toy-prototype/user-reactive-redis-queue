package com.jaewoo.queueserver.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer

@TestConfiguration
class EmbeddedRedisTestConfig(
    private val redisServer: RedisServer = RedisServer(63790)
) {
    @PostConstruct
    fun start() = redisServer.start()

    @PreDestroy
    fun end() = redisServer.stop()
}