package com.interactionfields.common.exception

import com.interactionfields.common.response.C
import com.interactionfields.common.response.R
import mu.KotlinLogging
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * The global exception handler.
 *
 * @author Ashinch
 * @date 2021/08/18
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    /**
     * [Exception] handler.
     */
    @ExceptionHandler(Exception::class)
    fun exceptionHandler(e: Exception): R? {
        logger.error(e.message, e)
        return R.failure(e.message)
    }

    /**
     * [BindException] handler.
     */
    @ExceptionHandler(BindException::class)
    fun validationExceptionHandler(e: BindException): R {
        val map = mutableMapOf<String, String?>()
        e.bindingResult.allErrors.forEach { map[(it as FieldError).field] = it.defaultMessage }
        return R.with(C.BAD_REQUEST, map)
    }

//    /**
//     * [InvalidGrantException] handler.
//     */
//    @ExceptionHandler(InvalidGrantException::class)
//    fun handleInvalidGrantException(e: InvalidGrantException): R {
//        return R.with(C.BAD_CREDENTIALS)
//    }
//
//    /**
//     * [InvalidGrantException] handler.
//     */
//    @ExceptionHandler(InternalAuthenticationServiceException::class)
//    fun handleInvalidGrantException(e: InternalAuthenticationServiceException): R {
//        return R.with(C.ACCOUNT_LOCKED)
//    }

    /**
     * [AccessDeniedException] handler.
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun accessDeniedExceptionHandler(e: AccessDeniedException): R {
        return R.with(C.ACCESS_DENIED)
    }
}
