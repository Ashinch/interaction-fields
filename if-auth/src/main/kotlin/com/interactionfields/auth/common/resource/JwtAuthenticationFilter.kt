package com.interactionfields.auth.common.resource

import com.interactionfields.auth.common.userdetails.Token
import com.interactionfields.auth.common.util.JWTExt.getTokenSignature
import com.interactionfields.auth.common.util.contextAuthToken
import com.interactionfields.common.extension.JsonExt.toObj
import com.interactionfields.common.extension.WebExt.write
import com.interactionfields.common.response.C
import com.interactionfields.common.response.R
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.jwt.JwtHelper
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(private val redisTemplate: StringRedisTemplate) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val header = (request as HttpServletRequest).getHeader("Authorization")
        if (header != null && header.isNotBlank() && !header.startsWith("null")) {
            JwtHelper.decode(header.replace("Bearer ", "")).let {
                val token: Token = it.claims.toObj(Token::class.java) as Token
                if (redisTemplate.boundSetOps("token.${token.user_name}")
                        .isMember(it.encoded.getTokenSignature()) == false
                ) {
                    (response as HttpServletResponse).write(R.with(C.BAD_CLIENT_CREDENTIALS))
                    return
                }
                contextAuthToken = it.encoded
            }
        }
        chain?.doFilter(request, response)
    }
}
