package com.interactionfields.auth.common.resource

import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Resource Server Configuration.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Configuration
@EnableResourceServer
class ResourceServerConfiguration(
    private val tokenStore: TokenStore,
    private val redisTemplate: StringRedisTemplate
) : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/webrtc").authenticated()
            .anyRequest().permitAll()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterAfter(JwtAuthenticationFilter(redisTemplate), BasicAuthenticationFilter::class.java)
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenStore(tokenStore)
    }
}
