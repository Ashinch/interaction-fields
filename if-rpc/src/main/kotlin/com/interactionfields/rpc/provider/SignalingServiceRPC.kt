package com.interactionfields.rpc.provider

import com.interactionfields.rpc.dto.CommitResultDTO
import com.interactionfields.rpc.interceptor.FeignClientInterceptor
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * RPC function about the meeting service.
 */
@FeignClient(value = "signaling-service", configuration = [FeignClientInterceptor::class])
interface SignalingServiceRPC {

    @PostMapping("/broadcast/judgeResult")
    fun judgeResult(
        @RequestParam code: String,
        @RequestParam commitResultDTO: CommitResultDTO
    )
}
