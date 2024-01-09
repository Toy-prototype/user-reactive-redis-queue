package com.jaewoo.queueserver.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.util.function.Tuples

@Service
class ScheduleService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val userQueueService: UserQueueService,
    @Value("\${scheduler.enabled}")
    private val scheduling: Boolean
) {

    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 1000 * 3)
    fun scheduleAllowUser() {
        if(scheduling.not()) return

        val maxAllowCount = 3L
        reactiveRedisTemplate.scan(ScanOptions.scanOptions().match("users:queue:*:wait").count(100).build())
            .map { it.split(":")[2] }
            .flatMap {
                    queueKey -> userQueueService.approval(queueKey, maxAllowCount).map { allowed -> Tuples.of(queueKey, allowed ) }
            }
            .doOnNext { println("Try $maxAllowCount and allowed ${it.t2}  member of ${it.t1} queue") }
            .subscribe()
    }
}