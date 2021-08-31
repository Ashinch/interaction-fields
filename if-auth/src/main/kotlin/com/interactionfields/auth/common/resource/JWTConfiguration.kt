package com.interactionfields.auth.common.resource

import com.interactionfields.auth.common.userdetails.UserDetailsService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.util.FileCopyUtils

/**
 * JWT token enhancer for the resource server.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Configuration
class JWTConfiguration(private val userDetailsService: UserDetailsService) {

    @Bean
    @Qualifier("tokenStore")
    fun tokenStore(): TokenStore = JwtTokenStore(jwtTokenEnhancer())

    @Bean
    fun jwtTokenEnhancer(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        // Set up the Principal
        DefaultUserAuthenticationConverter().run {
            setUserDetailsService(userDetailsService)
            (converter.accessTokenConverter as DefaultAccessTokenConverter).setUserTokenConverter(this)
        }
        // Set up the public key
        val publicKey = String(FileCopyUtils.copyToByteArray(ClassPathResource("public.cert").inputStream))
        converter.setVerifierKey(publicKey)
        return converter
    }
}
