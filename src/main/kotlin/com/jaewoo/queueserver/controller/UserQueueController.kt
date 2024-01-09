package com.jaewoo.queueserver.controller

import com.jaewoo.queueserver.controller.res.ApproveUserResponse
import com.jaewoo.queueserver.controller.res.RegisterUserResponse
import com.jaewoo.queueserver.service.UserQueueService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserQueueController(
    private val userQueueService: UserQueueService
) {
    @PostMapping("/queue")
    fun registerWaitQueue(
        @RequestParam queueKey: String,
        @RequestParam userId: Long
    ): Mono<RegisterUserResponse> {
        return userQueueService.registerWaitQueue(queueKey, userId)
            .map { RegisterUserResponse(it) }
    }

    @PostMapping("/approval")
    fun approval(
        @RequestParam queueKey: String,
        @RequestParam count: Long
    ): Mono<ApproveUserResponse> {
        return userQueueService.approval(queueKey, count)
            .map { ApproveUserResponse(count, it) }
    }

    @GetMapping("/approved")
    fun isApproval(
        @RequestParam queueKey: String,
        @RequestParam userId: Long
    ): Mono<Any> {
        return userQueueService.isApproval(queueKey, userId)
            .map { it }
    }

}