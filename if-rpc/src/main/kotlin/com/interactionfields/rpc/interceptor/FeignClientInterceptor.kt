package com.interactionfields.rpc.interceptor

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Request interceptor for Feign client.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Component
class FeignClientInterceptor : RequestInterceptor {

    /**
     * Convey the actual token in the request header.
     */
    override fun apply(requestTemplate: RequestTemplate) {
        val requestAttributes = RequestContextHolder.currentRequestAttributes()
        val request = (requestAttributes as ServletRequestAttributes).request
        val token = request.getHeader("Authorization")
        requestTemplate.header("Authorization", token)
    }
}
