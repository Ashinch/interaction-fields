package com.interactionfields.auth.common.userdetails

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

/**
 * Apply for a token from the authentication server using the RPC function.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@FeignClient("auth-service")
interface AuthServiceRPC {

    @PostMapping("/oauth/token")
    fun getToken(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("grant_type") grant_type: String,
        @RequestParam("username") username: String,
        @RequestParam("password") password: String
    ): JWT?

    @PostMapping("/oauth/token")
    fun refreshToken(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam("grant_type") grant_type: String? = "refresh_token",
        @RequestParam("refresh_token") refreshToken: String
    ): JWT?
}
