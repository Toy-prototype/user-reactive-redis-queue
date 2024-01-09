package com.jaewoo.queueserver.exception

import org.springframework.http.HttpStatus

class ApplicationException(
    val httpStatus: HttpStatus,
    val code: String,
    val reason: String
): RuntimeException()