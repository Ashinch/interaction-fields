package com.interactionfields.auth

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
) : AuthorizationServerConfigurerAdapter() {

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
            .withClient("user-service")
            .secret("276364092".encodeBCrypt())
            .scopes("service")
            .authorizedGrantTypes("refresh_token", "password")
            .accessTokenValiditySeconds(3600 * 1000)
            .and()
            .withClient("meeting-service")
            .secret("276364092".encodeBCrypt())
            .scopes("service")
            .authorizedGrantTypes("refresh_token", "password")
            .accessTokenValiditySeconds(3600 * 1000)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
            .tokenEnhancer(jwtTokenEnhancer())
            .authenticationManager(authenticationManager)
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
            val factory = KeyStoreKeyFactory(ClassPathResource("oauth2-jwt.jks"), "276364092".toCharArray())
            setKeyPair(factory.getKeyPair("oauth2-jwt", "276364092".toCharArray()))
        }
    }
}
