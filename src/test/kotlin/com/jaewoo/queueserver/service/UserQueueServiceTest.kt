package com.jaewoo.queueserver.service

import com.jaewoo.queueserver.config.EmbeddedRedisTestConfig
import com.jaewoo.queueserver.exception.ApplicationException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(EmbeddedRedisTestConfig::class)
@ActiveProfiles("test")
class UserQueueServiceTest(
) {

    @Autowired
    private lateinit var userQueueService: UserQueueService

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @BeforeEach
    fun before() {
        val redisConnection = reactiveRedisTemplate.connectionFactory.reactiveConnection
        redisConnection.serverCommands().flushAll().subscribe()
    }

    @DisplayName("대기열에 2명의 유저를 등록한다.")
    @Test
    fun registerWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 1000L))
            .expectNext(1L)
            .verifyComplete()

        StepVerifier.create(userQueueService.registerWaitQueue("default", 1001L))
            .expectNext(2L)
            .verifyComplete()
    }

    @DisplayName("이미 대기열에 있는 유저가 대기열에 들어올 경우 에러를 발생시킨다.")
    @Test
    fun alreadyWaitQueue() {
        StepVerifier.create(userQueueService.registerWaitQueue("default", 1000L))
            .expectNext(1L)
            .verifyComplete()

        StepVerifier.create(userQueueService.registerWaitQueue("default", 1000L))
            .expectError(ApplicationException::class.java)
            .verify()
    }

    @DisplayName("대기열이 비어있을때 3명의 유저를 승인한다.")
    @Test
    fun approval() {
        StepVerifier.create(userQueueService.approval("default", 3L))
            .expectNext(0L)
            .verifyComplete()
    }

    @DisplayName("대기열에 3명의 유저가 있을 경우 2명의 유저를 승인한다.")
    @Test
    fun approval2() {
        StepVerifier.create(
            userQueueService.registerWaitQueue("default", 1000L)
                .then(userQueueService.registerWaitQueue("default", 1001L))
                .then(userQueueService.registerWaitQueue("default", 1002L))
                .then(userQueueService.approval("default", 2L))
        )
            .expectNext(2L)
            .verifyComplete()
    }

    @Test
    fun isNotApproval() {
        StepVerifier.create(userQueueService.isApproval("default", 1001L))
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun isNotApprovalUserId() {
        StepVerifier.create(
            userQueueService.registerWaitQueue("default", 1000L)
                .then(userQueueService.approval("default", 2L))
                .then(userQueueService.isApproval("default", 1001L)))
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun isApproval() {
        StepVerifier.create(
            userQueueService.registerWaitQueue("default", 1000L)
                .then(userQueueService.approval("default", 2L))
                .then(userQueueService.isApproval("default", 1000L)))
            .expectNext(true)
            .verifyComplete()
    }
}