package com.interactionfields.rpc.provider

import com.interactionfields.common.response.R
import com.interactionfields.rpc.interceptor.FeignClientInterceptor
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

/**
 * RPC function about the meeting service.
 */
@FeignClient(value = "meeting-service", configuration = [FeignClientInterceptor::class])
interface MeetingServiceRPC {
    @PostMapping("/meeting/create")
    fun create(): R
}
