package com.jaewoo.queueserver.service

import com.jaewoo.queueserver.exception.ErrorCode
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.function.Tuples
import java.time.Instant

@Service
class UserQueueService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {

    private val USER_QUEUE_WAIT_KEY = "users:queue:%s:wait"
    private val USER_QUEUE_APPROVAL_KEY = "users:queue:%s:approval"

    fun registerWaitQueue(queueKey: String, userId: Long): Mono<Long> {
        val unixTimeStamp = Instant.now().epochSecond
        return reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.format(queueKey), userId.toString(), unixTimeStamp.toDouble())
            .filter { i-> i }
            .switchIfEmpty(Mono.error(ErrorCode.USER_ALREADY_IN_QUEUE.build()))
            .flatMap { reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.format(queueKey), userId.toString()) }
            .map { when (it >= 0) {
                true -> it + 1
                false -> it
            } }
    }


    fun approval(queueKey: String, count: Long): Mono<Long> {
        return reactiveRedisTemplate.opsForZSet().popMin(USER_QUEUE_WAIT_KEY.format(queueKey), count)
            .flatMap {
                reactiveRedisTemplate
                    .opsForZSet()
                    .add(USER_QUEUE_APPROVAL_KEY.format(queueKey), it.value!!, Instant.now().epochSecond.toDouble())
            }
            .count()
    }

    fun isApproval(queueKey: String, userId: Long): Mono<Boolean> {
        return reactiveRedisTemplate.opsForZSet()
            .rank(USER_QUEUE_APPROVAL_KEY.format(queueKey), userId.toString())
            .defaultIfEmpty(-1L)
            .map { it >= 0 }
    }
}