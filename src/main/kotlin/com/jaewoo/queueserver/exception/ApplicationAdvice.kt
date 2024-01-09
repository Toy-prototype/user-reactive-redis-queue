package com.jaewoo.queueserver.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.MissingRequestValueException
import reactor.core.publisher.Mono

@RestControllerAdvice
class ApplicationAdvice {

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(e: ApplicationException): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity
                .status(e.httpStatus)
                .body(ServerErrorResponse(e.code, e.reason))
        )
    }

    @ExceptionHandler(MissingRequestValueException::class)
    fun handleMissingRequestValueException(e: MissingRequestValueException): Mono<ResponseEntity<Any>> {
        return Mono.just(
            ResponseEntity
                .status(e.statusCode)
                .body(ServerErrorResponse(e.statusCode.value().toString(), e.reason!!))
        )
    }

    class ServerErrorResponse(val code: String, val reason: String)
}