package com.interactionfields.auth.common.resource

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

/**
 * Resource Server Configuration.
 *
 * @author Ashinch
 * @date 2021/08/25
 */
@Configuration
@EnableResourceServer
class ResourceServerConfiguration(private val tokenStore: TokenStore) : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/webrtc").authenticated()
            .anyRequest().permitAll()
    }

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenStore(tokenStore)
    }
}
