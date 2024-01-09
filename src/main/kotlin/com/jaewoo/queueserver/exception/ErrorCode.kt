package com.jaewoo.queueserver.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val reason: String
) {
    USER_ALREADY_IN_QUEUE(HttpStatus.BAD_REQUEST, "USER_ALREADY_IN_QUEUE", "User already in queue");

    fun build(): ApplicationException = ApplicationException(this.httpStatus, this.code, this.reason)
}