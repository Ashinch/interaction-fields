package com.interactionfields.auth

import com.interactionfields.auth.common.userdetails.UserDetailsService
import com.interactionfields.auth.common.util.BCryptPasswordEncoderExt.encodeBCrypt
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory

/**
 * Authentication Server Configuration.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Configuration
@EnableAuthorizationServer
class OAuth2Configuration(
    @Qualifier("authenticationManagerBean")
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
) : AuthorizationServerConfigurerAdapter() {

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
            .withClient("client")
            .secret("276364092".encodeBCrypt())
            .scopes("service")
            .authorizedGrantTypes("refresh_token", "password")
            .accessTokenValiditySeconds(60 * 60 * 24)
            .refreshTokenValiditySeconds(60 * 60 * 24 * 7)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
            .tokenEnhancer(jwtTokenEnhancer())
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService)
    }

    /**
     * Configure the token storage mode to JwtTokenStore.
     */
    @Bean
    fun tokenStore(): TokenStore = JwtTokenStore(jwtTokenEnhancer())

    /**
     * Configure the enhancer for JWT private key encryption.
     */
    @Bean
    protected fun jwtTokenEnhancer(): JwtAccessTokenConverter {
        return JwtAccessTokenConverter().apply {
            val factory = KeyStoreKeyFactory(ClassPathResource("ca.jks"), "@Ash1nch".toCharArray())
            setKeyPair(factory.getKeyPair("1", "@Ash1nch".toCharArray()))
        }
    }
}
